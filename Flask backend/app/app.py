from flask import Flask,request,jsonify
import redis
import secrets

app = Flask(__name__)
redis_client = redis.StrictRedis(host='redis', port=6379, decode_responses=True)

@app.post("/event")
def endpoint_event():
    new_key = "event_" + str(redis_client.incr("event_incr"))
    admin_token = secrets.token_hex(3)
    
    #@TODO: This should probably use a transaction.
    is_created = redis_client.sadd("event_names", new_key)
    if not is_created:
        return {"error": "I can't tell you the cause of this error."},400

    is_created = redis_client.hset("tokens",new_key,admin_token)
    if not is_created:
        redis_client.srem("event_names",new_key)
        return {"error": "I can't tell you the cause of this error."},400

    return {"event": new_key,"admin_token": admin_token},201

def does_event_exist(event_name):
    return redis_client.sismember("event_names", event_name)

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

    redis_client.xtrim(event_name,minid='0-0')
    redis_client.hdel("tokens",event_name)
    redis_client.srem("event_names",event_name)
    return {"success": 1},200

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

if __name__ == "__main__":
    app.run(host="0.0.0.0")
