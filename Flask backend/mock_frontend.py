import sys
import base64
import requests

address = "http://127.0.0.1:8080"
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
    def addr(name,*args):
        global address
        if len(args) == 0:
            print(address)
            return
        if len(args) != 1:
            raise Exception("Syntax: addr [address]")
        address = args[0]

    @staticmethod
    def genevent(name,*args):
        if len(args) != 0:
            raise Exception("Syntax: genevent")
        make_http_request("post","event",{})

    @staticmethod
    def delevent(name,*args):
        if len(args) != 2:
            raise Exception("Syntax: delevent <event_name> <admin_token>")
        make_http_request("delete",f"event/{args[0]}",{"token": args[1]})

    @staticmethod
    def listevents(name,*args):
        if len(args) != 0:
            raise Exception("Syntax: listevents")
        make_http_request("get","list",{})

    @staticmethod
    def addimage(name,*args):
        if len(args) != 2:
            raise Exception("Syntax: addimage <event_name> <path_to_image>")
        with open(args[1],"rb") as file:
            content = base64.b64encode(file.read()).decode("utf-8")
            make_http_request("post",f"images/{args[0]}",{"b64": content})

    @staticmethod
    def remimage(name,*args):
        if len(args) != 3:
            raise Exception("Syntax: remimage <event_name> <image_id> <admin_token>")
        make_http_request("delete",f"images/{args[0]}/{args[1]}",{"token": args[2]})

    @staticmethod
    def getimage(name,*args):
        if len(args) < 2 or len(args) > 3:
            raise Exception("Syntax: getimage <event_name> <image_id> [path_to_output_image]")
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
