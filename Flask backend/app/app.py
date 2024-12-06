import sys
import redis
import secrets
import werkzeug
import werkzeug.exceptions
from flask import Flask,request

ErrorCodeIncorrectUserToken = 0
ErrorCodeIncorrectAdminToken = 1
ErrorCodeStringTooLong = 2
ErrorCodeStringFailedFiltering = 3
ErrorCodeInternalError = 4
ErrorCodeImageWidthInvalid = 5
ErrorCodeImageHeightInvalid = 6

def success(value):
    return {"success": True,"params": value},200

def error(code: int):
    return {"success": False,"params": code},200

app = Flask(__name__)
database = redis.StrictRedis(host = "redis",port = 6379,decode_responses = True)

#@TODO: User provided strings should be filtered in some way to at least prevent basic swears or bad phrases.
def filter_user_string(string: str) -> bool:
    return True

#@TODO: This should log to a file.
def log_endpoint_error(string: str):
    print(string,file = sys.stderr)

def does_event_exist(user_token: str) -> bool:
    return database.sismember("user_tokens",user_token) == 1

def is_admin_token_valid(user_token: str,admin_token: str) -> bool:
    return database.hget("admin_tokens",user_token) == admin_token

@app.errorhandler(werkzeug.exceptions.InternalServerError)
def handle_internal_server_error(error):
    log_endpoint_error(str(error))
    return {"success": False,"params": ErrorCodeInternalError},500

@app.errorhandler(werkzeug.exceptions.NotFound)
def handle_not_found_error(error):
    log_endpoint_error(str(error))
    return {"success": False,"params": ErrorCodeInternalError},404

@app.get("/event/<user_token>")
def endpoint_event_check(user_token: str):
    if not does_event_exist(user_token):
        return error(ErrorCodeIncorrectUserToken)
    return success({"event_name": database.hget("event_names",user_token)})

@app.post("/auth/<user_token>")
def endpoint_auth_event(user_token: str):
    if not does_event_exist(user_token):
        return error(ErrorCodeIncorrectUserToken)
    if "admin_token" in request.json:
        admin_token = str(request.json["admin_token"])
        if admin_token != "":
            if not is_admin_token_valid(user_token,admin_token):
                return error(ErrorCodeIncorrectAdminToken)
    return success({})

@app.post("/event")
def endpoint_event():
    if "event_name" not in request.json:
        return error(ErrorCodeInternalError)
    event_name = str(request.json["event_name"])
    if len(event_name) > 100:
        return error(ErrorCodeStringTooLong)
    if not filter_user_string(event_name):
        return error(ErrorCodeStringFailedFiltering)

    user_token = "event_" + str(database.incr("user_token_increment"))
    admin_token = secrets.token_hex(3)

    transaction = database.pipeline()
    transaction.sadd("user_tokens",user_token)
    transaction.hset("admin_tokens",user_token,admin_token)
    transaction.hset("event_names",user_token,event_name)
    transaction.execute(raise_on_error = True)
    return success({"user_token": user_token,"admin_token": admin_token})

@app.delete("/event/<user_token>")
def endpoint_event_delete(user_token: str):
    print(str(request.json))
    if "admin_token" not in request.json:
        return error(ErrorCodeInternalError)
    if not does_event_exist(user_token):
        return error(ErrorCodeIncorrectUserToken)
    admin_token = str(request.json["admin_token"])
    if not is_admin_token_valid(user_token,admin_token):
        return error(ErrorCodeIncorrectAdminToken)

    transaction = database.pipeline()
    transaction.xtrim(user_token,minid = "0-0")
    transaction.hdel("event_names",user_token)
    transaction.hdel("admin_tokens",user_token)
    transaction.srem("user_tokens",user_token)
    transaction.execute(raise_on_error = True)
    return success({})

@app.get("/getimagecount/<user_token>")
def endpoint_get_image_count(user_token: str):
    if not does_event_exist(user_token):
        return error(ErrorCodeIncorrectUserToken)
    return success(database.xlen(user_token))

@app.post("/images/<user_token>")
def endpoint_images_add(user_token: str):
    if not does_event_exist(user_token):
        return error(ErrorCodeIncorrectUserToken)

    width = 0
    height = 0
    if "width" not in request.json:
        return error(ErrorCodeInternalError)
    if "height" not in request.json:
        return error(ErrorCodeInternalError)

    try:
        width = int(request.json["width"])
    except ValueError:
        return error(ErrorCodeImageWidthInvalid)
    if width < 0 or width >= 16384:
        return error(ErrorCodeImageWidthInvalid)
    try:
        height = int(request.json["height"])
    except ValueError:
        return error(ErrorCodeImageHeightInvalid)
    if height < 0 or height >= 16384:
        return error(ErrorCodeImageHeightInvalid)

    if "pixels" not in request.json:
        return error(ErrorCodeInternalError)
    pixels = str(request.json["pixels"])

    title = ""
    if "title" in request.json:
        title = str(request.json["title"])
        if len(title) > 100:
            return error(ErrorCodeStringTooLong)
        if not filter_user_string(title):
            return error(ErrorCodeStringFailedFiltering)

    image_id = database.xadd(user_token,{
        "width": width,
        "height": height,
        "pixels": pixels,
        "title": title
    })
    return success({"image_id": image_id})

@app.delete("/images/<user_token>/<image_id>")
def endpoint_images_delete(user_token: str,image_id: str):
    if "admin_token" not in request.json:
        return error(ErrorCodeInternalError)
    if not does_event_exist(user_token):
        return error(ErrorCodeIncorrectUserToken)

    admin_token = str(request.json["admin_token"])
    if not is_admin_token_valid(user_token,admin_token):
        return error(ErrorCodeIncorrectAdminToken)

    database.xdel(user_token,image_id)
    return success({})

@app.get("/images/<user_token>/<image_id>")
def endpoint_images_get(user_token: str,image_id: str):
    if not does_event_exist(user_token):
        return error(ErrorCodeIncorrectUserToken)

    stream = database.xrange(user_token,image_id,"+")
    data = []
    for identifier,attributes in stream:
        temp = {"image_id": identifier}
        temp.update(attributes)
        data.append(temp)
    return success(data)