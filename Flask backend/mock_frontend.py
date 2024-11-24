import sys
import base64
import requests

def http_request(method: str,string: str,json: dict,printResponse: bool = True) -> dict:
    print(f"Request: {string}")
    url = f"http://127.0.0.1:8080/{string}"
    response = None
    if method == "get":
        response = requests.get(url = url,json = json)
    elif method == "post":
        response = requests.post(url = url,json = json)
    elif method == "delete":
        response = requests.delete(url = url,json = json)
    assert response != None
    data = response.json()
    if printResponse:
        print(f"Response: {data}")
    return data

def cmd_exit(name,*args):
    if len(args) != 0:
        raise Exception("Syntax: exit")
    sys.exit(0)

def cmd_genevent(name,*args):
    if len(args) != 1:
        raise Exception("Syntax: genevent <event_name>")
    http_request("post","event",{"event_name": args[0]})

def cmd_delevent(name,*args):
    if len(args) != 2:
        raise Exception("Syntax: delevent <user_token> <admin_token>")
    http_request("delete",f"event/{args[0]}",{"admin_token": args[1]})

def cmd_addimage(name,*args):
    if len(args) != 2 and len(args) != 3:
        raise Exception("Syntax: addimage <user_token> <path_to_image> [title]")
    with open(args[1],"rb") as file:
        content = base64.b64encode(file.read()).decode("utf-8")
        if len(args) == 2:
            http_request("post",f"images/{args[0]}",{"b64": content})
        else:
            http_request("post",f"images/{args[0]}",{"b64": content,"title": args[2]})

def cmd_remimage(name,*args):
    if len(args) != 3:
        raise Exception("Syntax: remimage <user_token> <image_id> <admin_token>")
    http_request("delete",f"images/{args[0]}/{args[1]}",{"admin_token": args[2]})

def cmd_getimage(name,*args):
    if len(args) != 2 and len(args) != 3:
        raise Exception("Syntax: getimage <user_token> <image_id> [images_to_file]")
    images = http_request("get",f"images/{args[0]}/{args[1]}",{},False)["params"]
    for image in images:
        print(f"{{\"image_id\": \"{image["image_id"]}\",\"title\": \"{image["title"]}\",\"b64\": [len: {len(image["b64"])}]}}")
        if len(args) == 3:
            content = base64.b64decode(bytes(image["b64"],encoding = "utf-8"))
            with open(f"{image["image_id"]}.jpg","wb") as file:
                file.write(content)

if __name__ == "__main__":
    commands = {
        "exit": cmd_exit,
        "genevent": cmd_genevent,
        "delevent": cmd_delevent,
        "addimage": cmd_addimage,
        "remimage": cmd_remimage,
        "getimage": cmd_getimage
    }
    while True:
        splitCmd = input("cmd: ").strip().split(" ")
        cmd = splitCmd[0]
        if cmd in commands:
            commands[cmd](*splitCmd)
        else:
            print(f"Command \"{cmd}\" doesn't exist.")
