from flask import Flask, request, jsonify
import redis


app = Flask(__name__)
redis_client = redis.StrictRedis(host='redis', port=6379, decode_responses=True)


# might need a decorator to handle connection

@app.route('/')
def hello():
    return "index"


# @app.route('/hihitest/<event_name>')
# def hihi(event_name:str):
#     with app.test_client() as client:
#         files = [
#             (open(r'C:\Users\mskrzyp\Desktop\Github\e.leclerc\s5-Android-APP\Flask backend\random jpgs\first.jpg', 'rb').read(), '1'),
#             (open(r'C:\Users\mskrzyp\Desktop\Github\e.leclerc\s5-Android-APP\Flask backend\random jpgs\second.jpg', 'rb').read(), '2')
#         ]
#         data = {
#             'files': files
#         }
#         response = client.post('/images/'+event_name, data=data, content_type='multipart/form-data')
#     return response


def gen_new_event():
    new_key = "event_" + str(redis_client.incr("event_incr"))
    is_created = redis_client.sadd("event_names", new_key)

    assert is_created  # TODO: handle this better

    return jsonify(data={"event_name": new_key}, status=201)


@app.route('/event', methods=['POST'])
def event_endpoint():
    return gen_new_event()


def does_event_exist(event_name):
    return redis_client.sismember("event_names", event_name)


def handle_images(event_name):
    if not does_event_exist(event_name):
        return "Event does not exist", 400

    data = request.json
    if "b64" not in data:
        return "b64 not in request", 400

    image_id = redis_client.xadd(event_name, {"b64": data["b64"]})
    return jsonify(data={"image_id": image_id}, status=201)


@app.route('/images/<event_name>', methods=["POST"])
def images_endpoint(event_name):
    return handle_images(event_name)


def from_images(event_name, image_id):
    if not does_event_exist(event_name):
        return "Event does not exist", 400

    smth = redis_client.xrange(event_name, image_id, '+')
    data = {"images": []}
    for image_id, attributes in smth:
        temp = {"image_id": image_id}
        temp.update(attributes)
        data["images"].append(temp)

    return jsonify(data=data, status=200)


@app.route('/images/<event_name>/<image_id>', methods=["GET"])
def from_images_endpoint(event_name, image_id):
    return from_images(event_name, image_id)


def delete_images(event_name, image_id):
    if not does_event_exist(event_name):
        return "Event does not exist", 400
    
    redis_client.xdel(event_name,image_id)

    return jsonify(data = "Success",success = 200)

@app.route('/images/<event_name>/<image_id>', methods=["DELETE"])
def delete_images_endpoint(event_name, image_id):
    return delete_images(event_name,image_id)

if __name__ == "__main__":
    app.run(host="0.0.0.0")
