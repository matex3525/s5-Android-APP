import sys
import time
import redis
import secrets
import werkzeug
import werkzeug.exceptions
from flask import Flask,request

class ErrorCode:
    IncorrectUserToken = 0
    IncorrectAdminToken = 1
    StringTooLong = 2
    StringFailedFiltering = 3
    InternalError = 4
    ImageWidthInvalid = 5
    ImageHeightInvalid = 6
    InvalidImageIndex = 7
    InvalidCommentIndex = 8

def success(value):
    return {"success": True,"params": value},200

def error(code: int):
    return {"success": False,"params": code},200

def image_id_list_name(user_token: str):
    return f"{user_token}_iid"

def image_stream_name(user_token: str):
    return f"{user_token}_i"

def image_comment_id_list_name(user_token: str,image_id: str):
    return f"{user_token}_{image_id}_cid"

def image_comment_stream_name(user_token: str,image_id: str):
    return f"{user_token}_{image_id}_c"

#@TODO: User provided strings should be filtered in some way to at least prevent basic swears or bad phrases.
def filter_user_string(string: str) -> bool:
    return True

#@TODO: This should log to a file.
def log_endpoint_error(string: str):
    print(string,file = sys.stderr)

app = Flask(__name__)
database = redis.StrictRedis(host = "redis",port = 6379,decode_responses = True)

def does_event_exist(user_token: str) -> bool:
    return database.sismember("user_tokens",user_token) == 1

def is_admin_token_valid(user_token: str,admin_token: str) -> bool:
    return database.hget("admin_tokens",user_token) == admin_token

@app.errorhandler(werkzeug.exceptions.InternalServerError)
def handle_internal_server_error(error):
    log_endpoint_error(str(error))
    return {"success": False,"params": ErrorCode.InternalError},500

@app.errorhandler(werkzeug.exceptions.NotFound)
def handle_not_found_error(error):
    log_endpoint_error(str(error))
    return {"success": False,"params": ErrorCode.InternalError},404

################################################################
#                     ENDPOINTS (EVENTS)                       #
################################################################

@app.post("/v0/event")
def endpoint_create_event():
    if "event_name" not in request.json:
        return error(ErrorCode.InternalError)
    event_name = str(request.json["event_name"])
    if len(event_name) > 128:
        return error(ErrorCode.StringTooLong)
    if not filter_user_string(event_name):
        return error(ErrorCode.StringFailedFiltering)

    user_token = "event_" + str(database.incr("user_token_incr"))
    admin_token = secrets.token_hex(3)

    transaction = database.pipeline()
    transaction.sadd("user_tokens",user_token)
    transaction.hset("admin_tokens",user_token,admin_token)
    transaction.hset("event_names",user_token,event_name)
    transaction.execute(raise_on_error = True)
    return success({"user_token": user_token,"admin_token": admin_token})

@app.delete("/v0/event/<user_token>")
def endpoint_delete_event(user_token: str):
    if "admin_token" not in request.json:
        return error(ErrorCode.InternalError)
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    admin_token = str(request.json["admin_token"])
    if not is_admin_token_valid(user_token,admin_token):
        return error(ErrorCode.IncorrectAdminToken)

    image_stream = database.xrange(image_stream_name(user_token),"0-0","+")
    transaction = database.pipeline()
    for image_id,_ in image_stream:
        transaction.ltrim(image_comment_id_list_name(user_token,image_id),0,-1)
        transaction.xtrim(image_comment_stream_name(user_token,image_id),minid = "0-0")
    transaction.ltrim(image_id_list_name(user_token),0,-1)
    transaction.xtrim(image_stream_name(user_token),minid = "0-0")
    transaction.hdel("event_names",user_token)
    transaction.hdel("admin_tokens",user_token)
    transaction.srem("user_tokens",user_token)
    transaction.execute(raise_on_error = True)
    return success({})

@app.get("/v0/event/<user_token>")
def endpoint_get_event_data(user_token: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    return success({"event_name": database.hget("event_names",user_token)})

@app.post("/v0/event/<user_token>/check")
def endpoint_check_admin_token(user_token: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    if "admin_token" not in request.json:
        return error(ErrorCode.InternalError)
    admin_token = str(request.json["admin_token"])
    if not is_admin_token_valid(user_token,admin_token):
        return error(ErrorCode.IncorrectAdminToken)
    return success({"event_name": database.hget("event_names",user_token)})

################################################################
#                     ENDPOINTS (IMAGES)                       #
################################################################

@app.get("/v0/event/<user_token>/imagecount")
def endpoint_get_image_count(user_token: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    return success(database.llen(image_id_list_name(user_token)))

@app.get("/v0/event/<user_token>/imageids/<first_index>/<last_index>")
def endpoint_get_image_ids(user_token: str,first_index: str,last_index: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    first_index = int(first_index)
    last_index = int(last_index)
    if (last_index < first_index and last_index != -1) or first_index < 0 or last_index < -1:
        return error(ErrorCode.InternalError)
    return success(database.lrange(image_id_list_name(user_token),first_index,last_index))

@app.post("/v0/event/<user_token>/image")
def endpoint_add_image(user_token: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)

    if "width" not in request.json:
        return error(ErrorCode.InternalError)
    width = int(request.json["width"])
    if width < 0 or width > 8192:
        return error(ErrorCode.ImageWidthInvalid)

    if "height" not in request.json:
        return error(ErrorCode.InternalError)
    height = int(request.json["height"])
    if height < 0 or height > 8192:
        return error(ErrorCode.ImageHeightInvalid)

    if "description" not in request.json:
        return error(ErrorCode.InternalError)
    description = str(request.json["description"])
    if len(description) > 128:
        return error(ErrorCode.StringTooLong)
    if not filter_user_string(description):
        return error(ErrorCode.StringFailedFiltering)

    if "pixels" not in request.json:
        return error(ErrorCode.InternalError)
    pixels = str(request.json["pixels"])

    #Using 'xadd' on a pipeline doesn't return stream ID.
    image_id = database.xadd(image_stream_name(user_token),{
        "width": width,
        "height": height,
        "description": description,
        "pixels": pixels
    })
    database.lpush(image_id_list_name(user_token),image_id)
    return success({"image_id": image_id})

@app.delete("/v0/event/<user_token>/image/byid/<image_id>")
def endpoint_delete_image(user_token: str,image_id: str):
    if "admin_token" not in request.json:
        return error(ErrorCode.InternalError)
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)

    admin_token = str(request.json["admin_token"])
    if not is_admin_token_valid(user_token,admin_token):
        return error(ErrorCode.IncorrectAdminToken)

    transaction = database.pipeline()
    transaction.ltrim(image_comment_id_list_name(user_token,image_id),0,-1)
    transaction.xtrim(image_comment_stream_name(user_token,image_id),minid = "0-0")
    transaction.lrem(image_id_list_name(user_token),0,image_id)
    transaction.xdel(image_stream_name(user_token),image_id)
    transaction.execute(raise_on_error = True)
    return success({})

@app.get("/v0/event/<user_token>/image/byindex/<image_index>")
def endpoint_get_image_by_index(user_token: str,image_index: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    tmp = database.lindex(image_id_list_name(user_token),image_index)
    if tmp is None:
        return error(ErrorCode.InvalidImageIndex)
    image_id = str(tmp)
    return endpoint_get_image_by_id(user_token,image_id)

@app.get("/v0/event/<user_token>/image/byid/<image_id>")
def endpoint_get_image_by_id(user_token: str,image_id: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    stream = database.xrange(image_stream_name(user_token),image_id,"+",1)
    data = None
    for identifier,attributes in stream:
        temp = {"image_id": identifier}
        temp.update(attributes)
        data = temp
    return success([data] if data is not None else [])

################################################################
#                    ENDPOINTS (COMMENTS)                      #
################################################################

@app.post("/v0/event/<user_token>/image/byid/<image_id>/comment")
def endpoint_add_comment(user_token: str,image_id: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)

    if "text" not in request.json:
        return error(ErrorCode.InternalError)
    text = str(request.json["text"])
    if len(text) > 4096:
        return error(ErrorCode.StringTooLong)
    if not filter_user_string(text):
        return error(ErrorCode.StringFailedFiltering)

    commentTime = time.time_ns() // 1000000
    comment_id = database.xadd(image_comment_stream_name(user_token,image_id),{
        "text": text,
        "time": commentTime
    })
    database.lpush(image_comment_id_list_name(user_token,image_id),comment_id)
    return success({"comment_id": comment_id,"time": commentTime})

@app.get("/v0/event/<user_token>/image/byid/<image_id>/commentcount")
def endpoint_get_image_comment_count(user_token: str,image_id: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    return success(database.llen(image_comment_id_list_name(user_token,image_id)))

@app.get("/v0/event/<user_token>/image/byid/<image_id>/commentids/<first_index>/<last_index>")
def endpoint_get_image_comment_ids(user_token: str,image_id: str,first_index: str,last_index: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    first_index = int(first_index)
    last_index = int(last_index)
    if (last_index < first_index and last_index != -1) or first_index < 0 or last_index < -1:
        return error(ErrorCode.InternalError)
    return success(database.lrange(image_comment_id_list_name(user_token,image_id),first_index,last_index))

@app.delete("/v0/event/<user_token>/image/byid/<image_id>/comment/byid/<comment_id>")
def endpoint_delete_comment_by_id(user_token: str,image_id: str,comment_id: str):
    if "admin_token" not in request.json:
        return error(ErrorCode.InternalError)
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)

    admin_token = str(request.json["admin_token"])
    if not is_admin_token_valid(user_token,admin_token):
        return error(ErrorCode.IncorrectAdminToken)

    transaction = database.pipeline()
    transaction.lrem(image_comment_id_list_name(user_token,image_id),0,comment_id)
    transaction.xdel(image_comment_stream_name(user_token,image_id),comment_id)
    transaction.execute(raise_on_error = True)
    return success({})

@app.get("/v0/event/<user_token>/image/byid/<image_id>/comment/byindex/<comment_index>")
def endpoint_get_image_comment_by_index(user_token: str,image_id: str,comment_index: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    tmp = database.lindex(image_comment_id_list_name(user_token,image_id),comment_index)
    if tmp is None:
        return error(ErrorCode.InvalidCommentIndex)
    comment_id = str(tmp)
    return endpoint_get_image_comment_by_id(user_token,image_id,comment_id)

@app.get("/v0/event/<user_token>/image/byid/<image_id>/comment/byid/<comment_id>")
def endpoint_get_image_comment_by_id(user_token: str,image_id: str,comment_id: str):
    if not does_event_exist(user_token):
        return error(ErrorCode.IncorrectUserToken)
    stream = database.xrange(image_comment_stream_name(user_token,image_id),comment_id,"+",1)
    data = None
    for identifier,attributes in stream:
        temp = {"comment_id": identifier}
        temp.update(attributes)
        data = temp
    return success([data] if data is not None else [])