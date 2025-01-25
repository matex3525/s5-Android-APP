import sys
import enum
import base64
import typing
import requests
from PIL import Image

class HttpMethod(enum.IntEnum):
    Get = 0
    Post = 1
    Delete = 2

def make_http_request(method: HttpMethod,url: str,json: dict,printResponse: bool = True) -> dict:
    def raise_invalid_method_exception():
        raise Exception(f"method == {method} (invalid value)") 
    print(f"Request: {url}")
    url = f"http://127.0.0.1:8080{url}"
    response = requests.get(url = url,json = json) if method == HttpMethod.Get else\
               requests.post(url = url,json = json) if method == HttpMethod.Post else\
               requests.delete(url = url,json = json) if method == HttpMethod.Delete else\
               raise_invalid_method_exception()
    data = response.json()
    if printResponse: print(f"Response: {data}")
    return data

def cmd_exit(name: str,*args: str):
    if len(args) != 0:
        raise Exception(f"Syntax: {name}")
    exit(0)

def cmd_help(name: str,*args: str):
    if len(args) != 0:
        raise Exception(f"Syntax: {name}")
    print("(Help) All commands:")
    for endpoint in all_commands.keys():
        print(f"  {endpoint}")

def endpoint_create_event(name: str,*args: str):
    if len(args) != 1:
        raise Exception(f"Syntax: {name} (event_name)")
    make_http_request(HttpMethod.Post,"/v0/event",{"event_name": args[0]})

def endpoint_delete_event(name: str,*args: str):
    if len(args) != 2:
        raise Exception(f"Syntax: {name} (user_token) (admin_token)")
    make_http_request(HttpMethod.Delete,f"/v0/event/{args[0]}",{"admin_token": args[1]})

def endpoint_get_event_data(name: str,*args: str):
    if len(args) != 1:
        raise Exception(f"Syntax: {name} (user_token)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}",{})

def endpoint_auth_event(name: str,*args: str):
    if len(args) != 2:
        raise Exception(f"Syntax: {name} (user_token) (admin_token)")
    make_http_request(HttpMethod.Post,f"/v0/event/{args[0]}/check",{"admin_token": args[1]})

def endpoint_get_image_count(name: str,*args: str):
    if len(args) != 1:
        raise Exception(f"Syntax: {name} (user_token)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/imagecount",{})

def endpoint_get_image_ids(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (first_index: int) (last_index: int)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/imageids/{int(args[1])}/{int(args[2])}",{})

def flatten_list_of_iterables(data):
    return [y for x in data for y in x]

def endpoint_add_image(name: str,*args: str):
    if len(args) != 2 and len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (image_file_path) {{album_id}}")
    with Image.open(args[1]) as file:
        if file.mode != "RGB" and file.mode != "RGBA":
            raise Exception("Only RGB and RGBA files are supported.")
        pixels = bytearray(flatten_list_of_iterables([(pixel[0],pixel[1],pixel[2],pixel[3] if len(pixel) >= 4 else 255) for pixel in file.getdata()]))
        pixel_string = base64.b64encode(pixels).decode("utf-8")
        json_object = {
            "width": file.width,
            "height": file.height,
            "description": "(description)",
            "pixels": pixel_string
        }
        if len(args) == 3:
            json_object["album_id"] = str(args[2])
        make_http_request(HttpMethod.Post,f"/v0/event/{args[0]}/image",json_object)

def endpoint_delete_image(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (admin_token) (image_id)")
    make_http_request(HttpMethod.Delete,f"/v0/event/{args[0]}/image/byid/{args[2]}",{"admin_token": args[1]})

def print_image_response(response: dict):
    data = {"image_id": response["image_id"],"width": int(response["width"]),"height": int(response["height"]),"description": response["description"],"pixels": f"({len(response["pixels"])} bytes in Base64)"}
    print(f"Response: {data}")

def check_value_is_bool(value: str) -> bool:
    def raise_bool_value():
        raise Exception(f"value == {value} (not a boolean value)")
    return False if value == "false" or value == "False" or value == "0" else True if value == "true" or value == "True" or value == "1" else raise_bool_value()

def show_image_from_base64_encoded_pixels(response: dict):
    pixels = base64.b64decode(str(response["pixels"]).encode("utf-8"))
    image = Image.frombytes(mode = "RGBA",size = (int(response["width"]),int(response["height"])),data = pixels)
    image.show()

def endpoint_get_image_by_index(name: str,*args: str):
    if len(args) != 2 and len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (image_index: int) {{show_image: bool}}")
    response = make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/image/byindex/{args[1]}",{},False)
    if int(response["success"]) == 0:
        raise Exception(f"Response: Error code: {response["params"]}")
    image_response = response["params"][0]
    print_image_response(image_response)
    if len(args) != 3: return
    if not check_value_is_bool(args[2]): return
    show_image_from_base64_encoded_pixels(image_response)

def endpoint_get_image_by_id(name: str,*args: str):
    if len(args) != 2 and len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (image_id) {{show_image: bool}}")
    response = make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/image/byid/{args[1]}",{},False)
    if int(response["success"]) == 0:
        raise Exception(f"Response: Error code: {response["params"]}")
    image_response = response["params"][0]
    print_image_response(image_response)
    if len(args) != 3: return
    if not check_value_is_bool(args[2]): return
    show_image_from_base64_encoded_pixels(image_response)

def endpoint_get_images_by_indices(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (first_image_index) (last_image_index)")
    response = make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/image/byindices/{args[1]}/{args[2]}",{},False)
    if int(response["success"]) == 0:
        raise Exception(f"Response: Error code: {response["params"]}")
    params = response["params"]
    for image_response in params:
        print_image_response(image_response)

def endpoint_get_image_thumb_by_index(name: str,*args: str):
    if len(args) != 2 and len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (image_index: int) {{show_image: bool}}")
    response = make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/imagethumbs/byindex/{args[1]}",{},False)
    if int(response["success"]) == 0:
        raise Exception(f"Response: Error code: {response["params"]}")
    image_response = response["params"][0]
    print_image_response(image_response)
    if len(args) != 3: return
    if not check_value_is_bool(args[2]): return
    show_image_from_base64_encoded_pixels(image_response)

def endpoint_get_image_thumb_by_id(name: str,*args: str):
    if len(args) != 2 and len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (image_id) {{show_image: bool}}")
    response = make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/imagethumbs/byid/{args[1]}",{},False)
    if int(response["success"]) == 0:
        raise Exception(f"Response: Error code: {response["params"]}")
    image_response = response["params"][0]
    print_image_response(image_response)
    if len(args) != 3: return
    if not check_value_is_bool(args[2]): return
    show_image_from_base64_encoded_pixels(image_response)

def endpoint_get_image_thumbs_by_indices(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (first_image_index) (last_image_index)")
    response = make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/imagethumbs/byindices/{args[1]}/{args[2]}",{},False)
    if int(response["success"]) == 0:
        raise Exception(f"Response: Error code: {response["params"]}")
    params = response["params"]
    for image_response in params:
        print_image_response(image_response)

def endpoint_add_comment(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (image_id) (comment_text)")
    make_http_request(HttpMethod.Post,f"/v0/event/{args[0]}/image/byid/{args[1]}/comment",{"text": args[2]})

def endpoint_get_image_comment_count(name: str,*args: str):
    if len(args) != 2:
        raise Exception(f"Syntax: {name} (user_token) (image_id)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/image/byid//{args[1]}/commentcount",{})

def endpoint_get_image_comment_ids(name: str,*args: str):
    if len(args) != 4:
        raise Exception(f"Syntax: {name} (user_token) (image_id) (first_index: int) (last_index: int)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/image/byid/{args[1]}/commentids/{args[2]}/{args[3]}",{})

def endpoint_delete_comment_by_id(name: str,*args: str):
    if len(args) != 4:
        raise Exception(f"Syntax: {name} (user_token) (admin_token) (image_id) (comment_id)")
    make_http_request(HttpMethod.Delete,f"/v0/event/{args[0]}/image/byid/{args[2]}/comment/byid/{args[3]}",{"admin_token": args[1]})

def endpoint_get_image_comment_by_index(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (image_id) (comment_index: int)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/image/byid/{args[1]}/comment/byindex/{args[2]}",{})

def endpoint_get_image_comment_by_id(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (image_id) (comment_id)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/image/byid/{args[1]}/comment/byid/{args[2]}",{})

def endpoint_get_image_comments_by_indices(name: str,*args: str):
    if len(args) != 4:
        raise Exception(f"Syntax: {name} (user_token) (image_id) (first_comment_index) (last_comment_index)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/image/byid/{args[1]}/comment/byindices/{args[2]}/{args[3]}",{})

def endpoint_create_album(name: str,*args: str):
    if len(args) != 2:
        raise Exception(f"Syntax: {name} (user_token) (name)")
    make_http_request(HttpMethod.Post,f"/v0/event/{args[0]}/album",{"name": str(args[1])})

def endpoint_delete_album_by_id(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (admin_token) (album_id)")
    make_http_request(HttpMethod.Delete,f"/v0/event/{args[0]}/album/byid/{args[2]}",{"admin_token": str(args[1])})

def endpoint_get_albums_by_indices(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (first_album_index: int) (last_album_index: int)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/album/byindices/{int(args[1])}/{int(args[2])}",{})

def endpoint_get_album_by_index(name: str,*args: str):
    if len(args) != 2:
        raise Exception(f"Syntax: {name} (user_token) (album_index: int)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/album/byindex/{args[1]}",{})

def endpoint_get_album_by_id(name: str,*args: str):
    if len(args) != 2:
        raise Exception(f"Syntax: {name} (user_token) (album_id)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/album/byid/{args[1]}",{})

def endpoint_get_album_image_ids(name: str,*args: str):
    if len(args) != 4:
        raise Exception(f"Syntax: {name} (user_token) (album_id) (first_image_index: int) (last_image_index: int)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/album/byid/{args[1]}/imageids/{int(args[2])}/{int(args[3])}",{})

def endpoint_get_album_image_count(name: str,*args: str):
    if len(args) != 2:
        raise Exception(f"Syntax: {name} (user_token) (album_id)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/album/byid/{args[1]}/imagecount",{})

def endpoint_get_album_image_by_index(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (album_id) (image_index: int)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[0]}/album/byid/{args[1]}/image/byindex/{int(args[2])}",{})

def endpoint_get_album_image_thumb_by_index(name: str,*args: str):
    if len(args) != 3:
        raise Exception(f"Syntax: {name} (user_token) (album_id) (image_index: int)")
    make_http_request(HttpMethod.Get,f"/v0/event/{args[2]}/album/byid/{args[1]}/imagethumbs/byindex/{int(args[2])}",{})

all_commands = {
    "create_event": endpoint_create_event,
    "delete_event": endpoint_delete_event,
    "event_data": endpoint_get_event_data,
    "auth_event": endpoint_auth_event,
    "image_count": endpoint_get_image_count,
    "image_ids": endpoint_get_image_ids,
    "add_image": endpoint_add_image,
    "delete_image": endpoint_delete_image,
    "image_by_index": endpoint_get_image_by_index,
    "image_by_id": endpoint_get_image_by_id,
    "images_by_indices": endpoint_get_images_by_indices,
    "image_thumb_by_index": endpoint_get_image_thumb_by_index,
    "image_thumb_by_id": endpoint_get_image_thumb_by_id,
    "image_thumbs_by_indices": endpoint_get_image_thumbs_by_indices,
    "add_comment": endpoint_add_comment,
    "comment_count": endpoint_get_image_comment_count,
    "comment_ids": endpoint_get_image_comment_ids,
    "delete_comment_by_id": endpoint_delete_comment_by_id,
    "comment_by_index": endpoint_get_image_comment_by_index,
    "comment_by_id": endpoint_get_image_comment_by_id,
    "comments_by_indices": endpoint_get_image_comments_by_indices,
    "create_album": endpoint_create_album,
    "delete_album": endpoint_delete_album_by_id,
    "albums_by_indices": endpoint_get_albums_by_indices,
    "album_by_index": endpoint_get_album_by_index,
    "album_by_id": endpoint_get_album_by_id,
    "album_image_ids": endpoint_get_album_image_ids,
    "album_image_count": endpoint_get_album_image_count,
    "album_image_by_index": endpoint_get_album_image_by_index,
    "album_image_thumb_by_index": endpoint_get_album_image_thumb_by_index,
    "exit": cmd_exit,
    "help": cmd_help
}

if __name__ == "__main__":
    print("Type \"help\" to list all endpoints. () denote obligatory arguments, {} denote optional arguments. Arguments are strings unless otherwise noted.")
    while True:
        text = input("> ")
        elements = text.strip().split()
        cmd = elements[0]
        try:
            all_commands[cmd](*elements)
        except Exception as error:
            print(f"Error: {error}")