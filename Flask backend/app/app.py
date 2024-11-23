import redis
import secrets
from flask import Flask,request

app = Flask(__name__)
database = redis.StrictRedis(host = "redis",port = 6379,decode_responses = True)

#@TODO: Should endpoint functions consist of one big try-except block?
#@TODO: Errors should be logged instead of printed to standard output.

#@TODO: User provided strings should be filtered in some way to at least prevent basic swears or bad phrases.
def filter_user_string(string: str) -> bool:
    return True

#@TODO: This should log to a file.
def log_endpoint_error(string: str):
    print(string)

@app.post("/event")
def endpoint_event():
    try:
        if "event_name" not in request.json:
            return {"error": "Name isn't specified."},400
        event_name: str = request.json["event_name"]
        if len(event_name) > 100:
            return {"error": "Name is too long (max 100 characters)."},400
        if not filter_user_string(event_name):
            return {"error": "Name is unacceptable."},400

        user_token = "event_" + str(database.incr("user_token_increment"))
        admin_token = secrets.token_hex(3)

        transaction = database.pipeline()
        transaction.sadd("user_tokens",user_token)
        transaction.hset("admin_tokens",user_token,admin_token)
        transaction.hset("event_names",user_token,event_name)
        transaction.execute(raise_on_error = True)
        return {"user_token": user_token,"admin_token": admin_token},201
    except Exception as error:
        log_endpoint_error(str(error))
        return {"error": "Internal error."},400

@app.get("/list")
def endpoint_list_events():
    try:
        #I know this is stupid but nevertheless makes testing easier.
        if "secret_key" not in request.json:
            return {"error": "Internal error."},400
        if request.json["secret_key"] != "Konstytucja Rzeczypospolitej Polskiej":
            return {"error": "Internal error."},400

        events = {}
        for user_token in database.sscan_iter("user_tokens"):
            events[user_token] = database.hget("event_names",user_token)
        return {"events": events},200
    except Exception as error:
        log_endpoint_error(str(error))
        return {"error": "Internal error."},400

def does_event_exist(user_token: str) -> bool:
    return database.sismember("user_tokens",user_token)

def is_admin_token_valid(user_token: str,admin_token: str) -> bool:
    return database.hget("admin_tokens",user_token) == admin_token

@app.delete("/event/<user_token>")
def endpoint_event_delete(user_token: str):
    try:
        if not does_event_exist(user_token):
            return {"error": f"There's no event '{user_token}'."},400
        if "admin_token" not in request.json:
            return {"error": "Admin token is required."},400
        admin_token = request.json["admin_token"]
        if not is_admin_token_valid(user_token,admin_token):
            return {"error": "Invalid admin token."},400
        
        transaction = database.pipeline()
        transaction.xtrim(user_token,minid = "0-0")
        transaction.hdel("event_names",user_token)
        transaction.hdel("admin_tokens",user_token)
        transaction.srem("user_tokens",user_token)
        transaction.execute(raise_on_error = True)
        return {"success": "Event successfully removed."},200
    except Exception as error:
        log_endpoint_error(str(error))
        return {"error": "Internal error."},400

@app.post("/images/<user_token>")
def endpoint_images_add(user_token: str):
    try:
        if not does_event_exist(user_token):
            return {"error": f"There's no event '{user_token}'."},400
        if "b64" not in request.json:
            return {"error": "'b64' not in request."},400

        title = ""
        if "title" in request.json:
            title = request.json["title"]
            if len(title) > 100:
                return {"error": "Title is too long (max 100 characters)."},400
            if not filter_user_string(title):
                return {"error": "Title is unacceptable."},400

        image_id = database.xadd(user_token,{"b64": request.json["b64"],"title": title})
        return {"image_id": image_id},201
    except Exception as error:
        log_endpoint_error(str(error))
        return {"error": "Internal error."},400

@app.delete("/images/<user_token>/<image_id>")
def endpoint_images_delete(user_token: str,image_id: str):
    try:
        if not does_event_exist(user_token):
            return {"error": f"There's no event '{user_token}'."},400
        if "admin_token" not in request.json:
            return {"error": "Admin token is required."},400
        admin_token = request.json["admin_token"]
        if not is_admin_token_valid(user_token,admin_token):
            return {"error": "Invalid admin token."},400

        database.xdel(user_token,image_id)
        return {"success": "Image successfully removed."},200
    except Exception as error:
        log_endpoint_error(str(error))
        return {"error": "Internal error."},400

@app.get("/images/<user_token>/<image_id>")
def endpoint_images_get(user_token: str,image_id: str):
    try:
        if not does_event_exist(user_token):
            return {"error": f"There's no event '{user_token}'."},400
        stream = database.xrange(user_token,image_id,"+")
        data = {"images": []}
        for identifier,attributes in stream:
            temp = {"image_id": identifier}
            temp.update(attributes)
            data["images"].append(temp)
        return data,200
    except Exception as error:
        log_endpoint_error(str(error))
        return {"error": "Internal error."},400
