import sys
import base64
import requests

address = "http://127.0.0.1:5000"
def make_http_request(method,string,json):
    print(f"Request: {string}")
    url = f"{address}/{string}"
    response = None
    if method == "get":
        response = requests.get(url = url,json = json)
    elif method == "post":
        response = requests.post(url = url,json = json)
    elif method == "put":
        response = requests.put(url = url,json = json)
    elif method == "delete":
        response = requests.delete(url = url,json = json)
    assert response != None
    data = response.json()
    print(f"Response: {data}")
    return data

class Commands:
    @staticmethod
    def exit(name,*args):
        if len(args) != 0:
            raise Exception("Syntax: exit")
        sys.exit(0)

    @staticmethod
    def new(name,*args):
        if len(args) != 0:
            raise Exception("Syntax: new")
        make_http_request("post","event",{})

    @staticmethod
    def add(name,*args):
        if len(args) != 2:
            raise Exception("Syntax: add <event_name> <path_to_image>")
        with open(args[1],"rb") as file:
            content = base64.b64encode(file.read()).decode("utf-8")
            make_http_request("post",f"images/{args[0]}",{"b64": content})

    @staticmethod
    def get(name,*args):
        if len(args) < 2 or len(args) > 3:
            raise Exception("Syntax: get <event_name> <image_id> [path_to_output_image]")
        data = make_http_request("get",f"images/{args[0]}/{args[1]}",{})["data"]["images"]
        if len(data) == 1 and len(args) == 3:
            content = base64.b64decode(bytes(data[0]["b64"],encoding = "utf-8"))
            with open(args[2],"wb") as file:
                file.write(content)

if __name__ == "__main__":
    allCommands = [getattr(Commands,x) for x in dir(Commands) if x[0] != '_']
    while True:
        splitCmd = input("cmd: ").strip().split(" ")
        exists = False
        for command in allCommands:
            if command.__name__ == splitCmd[0]:
                try:
                    exists = True
                    command(*splitCmd)
                except Exception as error:
                    print(error)
        if exists == False:
            print(f"Command '{splitCmd[0]}' doesn't exist.")
