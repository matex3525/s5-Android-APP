from flask import Flask
import redis

app = Flask(__name__)
redis_client = redis.StrictRedis(host='redis', port=6379, decode_responses=True)


@app.route('/')
def hello():
    redis_client.incr('hits')
    return f"Hello! This page has been viewed {redis_client.get('hits')} time(s)."


if __name__ == "__main__":
    app.run(host="0.0.0.0")
