import redis
import secrets
import werkzeug
from enum import Enum
from flask import Flask,request

class ErrorCode(Enum):
    IncorrectUserToken = 0
    IncorrectAdminToken = 1
    StringTooLong = 2
    StringFailedFiltering = 3
    InternalError = 4

def success(value):
    return {"success": 1,"params": value},200

def error(code: int):
    return {"success": 0,"params": code},200

app = Flask(__name__)
database = redis.StrictRedis(host = "redis",port = 6379,decode_responses = True)

#@TODO: User provided strings should be filtered in some way to at least prevent basic swears or bad phrases.
def filter_user_string(string: str) -> bool:
    return True

#@TODO: This should log to a file.
def log_endpoint_error(string: str):
    print(string)

def does_event_exist(user_token: str) -> bool:
    return database.sismember("user_tokens",user_token)

def is_admin_token_valid(user_token: str,admin_token: str) -> bool:
    return database.hget("admin_tokens",user_token) == admin_token

@app.errorhandler(werkzeug.exceptions.InternalServerError)
def handle_bad_request(error):
    log_endpoint_error(str(error))
    return {"success": 0,"params": ErrorCode.InternalError},500

@app.get("/event/<user_token>")
def endpoint_event_check_if_exists(user_token: str):
    return success(1 if does_event_exist(user_token) else 0)

@app.post("/event")
def endpoint_event():
    if "event_name" not in request.json:
        return error(ErrorCode.InternalError)
    event_name: str = request.json["event_name"]
    if len(event_name) > 100:
        return error(ErrorCode.StringTooLong)
    if not filter_user_string(event_name):
        return error(ErrorCode.StringFailedFiltering)
    
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
    if "admin_token" not in request.json:
        return error(ErrorCode.InternalError)
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    admin_token = request.json["admin_token"]
    if not is_admin_token_valid(user_token,admin_token):
        return error(ErrorCode.IncorrectAdminToken)

    transaction = database.pipeline()
    transaction.xtrim(user_token,minid = "0-0")
    transaction.hdel("event_names",user_token)
    transaction.hdel("admin_tokens",user_token)
    transaction.srem("user_tokens",user_token)
    transaction.execute(raise_on_error = True)
    return success(None)

@app.post("/images/<user_token>")
def endpoint_images_add(user_token: str):
    if "b64" not in request.json:
        return error(ErrorCode.InternalError)
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)

    title = ""
    if "title" in request.json:
        title = request.json["title"]
        if len(title) > 100:
            return error(ErrorCode.StringTooLong)
        if not filter_user_string(title):
            return error(ErrorCode.StringFailedFiltering)

    image_id = database.xadd(user_token,{"b64": request.json["b64"],"title": title})
    return success({"image_id": image_id})

@app.delete("/images/<user_token>/<image_id>")
def endpoint_images_delete(user_token: str,image_id: str):
    if "admin_token" not in request.json:
        return error(ErrorCode.InternalError)
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    
    admin_token = request.json["admin_token"]
    if not is_admin_token_valid(user_token,admin_token):
        return error(ErrorCode.IncorrectAdminToken)

    database.xdel(user_token,image_id)
    return success(None)

@app.get("/images/<user_token>/<image_id>")
def endpoint_images_get(user_token: str,image_id: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    
    stream = database.xrange(user_token,image_id,"+")
    data = []
    for identifier,attributes in stream:
        temp = {"image_id": identifier}
        temp.update(attributes)
        data.append(temp)
    return success(data)