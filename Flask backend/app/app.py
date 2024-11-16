from flask import Flask,request
import redis
import secrets

app = Flask(__name__)
redis_client = redis.StrictRedis(host = "redis",port = 6379,decode_responses = True)

@app.get("/list")
def endpoint_list_events():
    names = []
    for name in redis_client.sscan_iter("event_names"):
        names.append(name)
    return {"events": names},200

@app.post("/event")
def endpoint_event():
    new_key = "event_" + str(redis_client.incr("event_incr"))
    admin_token = secrets.token_hex(3)
    try:
        transaction = redis_client.pipeline()
        transaction.sadd("event_names",new_key)
        transaction.hset("tokens",new_key,admin_token)
        transaction.execute(raise_on_error = True)
        return {"event": new_key,"admin_token": admin_token},201
    except Exception as error:
        print(str(error))
        return {"error": "Internal error."},400

def does_event_exist(event_name):
    return redis_client.sismember("event_names",event_name)

def is_admin_token_valid(event_name,token):
    return redis_client.hget("tokens",event_name) == token

@app.delete("/event/<event_name>")
def endpoint_event_delete(event_name):
    if not does_event_exist(event_name):
        return {"error": f"There's no event '{event_name}'."},400
    if "token" not in request.json:
        return {"error": "'token' not in request."},400
    if not is_admin_token_valid(event_name,request.json["token"]):
        return {"error": "Invalid token."},400

    try:
        transaction = redis_client.pipeline()
        transaction.xtrim(event_name,minid = "0-0")
        transaction.hdel("tokens",event_name)
        transaction.srem("event_names",event_name)
        transaction.execute(raise_on_error = True)
        return {"success": 1},200
    except Exception as error:
        print(str(error))
        return {"error": "Internal error."},400

@app.post("/images/<event_name>")
def endpoint_images_add(event_name):
    if not does_event_exist(event_name):
        return {"error": f"There's no event '{event_name}'."},400
    if "b64" not in request.json:
        return {"error": "'b64' not in request."},400

    image_id = redis_client.xadd(event_name, {"b64": request.json["b64"]})
    return {"image_id": image_id},201

@app.get("/images/<event_name>/<image_id>")
def endpoint_images_get(event_name,image_id):
    if not does_event_exist(event_name):
        return {"error": f"There's no event '{event_name}'."},400

    smth = redis_client.xrange(event_name, image_id, '+')
    data = {"images": []}
    for image_id, attributes in smth:
        temp = {"image_id": image_id}
        temp.update(attributes)
        data["images"].append(temp)
    return data,200

@app.delete("/images/<event_name>/<image_id>")
def endpoint_images_delete(event_name,image_id):
    if not does_event_exist(event_name):
        return {"error": f"There's no event '{event_name}'."},400
    if "token" not in request.json:
        return {"error": "'token' not in request."},400
    if not is_admin_token_valid(event_name,request.json["token"]):
        return {"error": "Invalid token."},400

    redis_client.xdel(event_name,image_id)
    return {"success": 1},200
