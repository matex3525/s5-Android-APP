import base64
import app_tests
from PIL import Image

def check_value_is_int(value,endpoint_name,name):
    try:
        x = int(value)
    except ValueError:
        raise Exception(f"{endpoint_name}: \"{name}\" got {value} but an int was required.")

def check_endpoint(function,*args: str):
    response = function(function.__name__,*args)
    if "success" not in response:
        raise Exception(f"{function.__name__}: \"response\" doesn't have \"success\".")
    if "params" not in response:
        raise Exception(f"{function.__name__}: \"response\" doesn't have \"params\".")
    if(int(response["success"]) == 0):
        raise Exception(f"{function.__name__}: Error: {response["params"]}")
    params = response["params"]
    if function.__name__ == "endpoint_create_event":
        if "user_token" not in params:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"user_token\".")
        if "admin_token" not in params:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"admin_token\".")
    elif function.__name__ == "endpoint_delete_event":
        pass
    elif function.__name__ == "endpoint_get_event_data":
        if "event_name" not in params:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"event_name\".")
    elif function.__name__ == "endpoint_auth_event":
        if "event_name" not in params:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"event_name\".")
    elif function.__name__ == "endpoint_get_image_count":
        check_value_is_int(params,function.__name__,"")
    elif function.__name__ == "endpoint_add_image":
        if "image_id" not in params:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"image_id\".")
    elif function.__name__ == "endpoint_delete_image":
        pass
    elif function.__name__ == "endpoint_get_image_by_index":
        if not isinstance(params,list):
            raise Exception(f"{function.__name__}: \"params\" isn't a list.")
        if len(params) != 1:
            raise Exception(f"{function.__name__}: len(\"params\") != 1.")
        param = params[0]
        if "image_id" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"image_id\".")
        if "width" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"width\".")
        check_value_is_int(param["width"],function.__name__,"width")
        if "height" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"height\".")
        check_value_is_int(param["height"],function.__name__,"height")
        if "description" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"description\".")
        if "pixels" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"pixels\".")
    elif function.__name__ == "endpoint_create_album":
        if "album_id" not in params:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"album_id\".")
        if "time" not in params:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"time\".")
        check_value_is_int(params["time"],function.__name__,"time")
    elif function.__name__ == "endpoint_delete_album_by_id":
        pass
    elif function.__name__ == "endpoint_get_album_by_id":
        if not isinstance(params,list):
            raise Exception(f"{function.__name__}: \"params\" isn't a list.")
        if len(params) != 1:
            raise Exception(f"{function.__name__}: len(\"params\") != 1.")
        param = params[0]
        if "album_id" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"album_id\".")
        if "name" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"name\".")
        if "image_count" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"image_count\".")
        check_value_is_int(param["image_count"],function.__name__,"image_count")
        if "time" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"time\".")
        check_value_is_int(param["time"],function.__name__,"time")
    elif function.__name__ == "endpoint_get_album_image_count":
        check_value_is_int(params,function.__name__,"")
    elif function.__name__ == "endpoint_get_album_image_by_index":
        if not isinstance(params,list):
            raise Exception(f"{function.__name__}: \"params\" isn't a list.")
        if len(params) != 1:
            raise Exception(f"{function.__name__}: len(\"params\") != 1.")
        param = params[0]
        if "image_id" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"image_id\".")
        if "width" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"width\".")
        check_value_is_int(param["width"],function.__name__,"width")
        if "height" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"height\".")
        check_value_is_int(param["height"],function.__name__,"height")
        if "description" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"description\".")
        if "pixels" not in param:
            raise Exception(f"{function.__name__}: \"params\" doesn't have \"pixels\".")
    return params

def check_values_equal(got_value,required_value,endpoint_name: str,name: str):
    if got_value != required_value:
        raise Exception(f"{endpoint_name}: \"{name}\" got {got_value} but required {required_value}.")

def test_event_cqd():
    event_name = "Konstytucja Rzeczypospolitej Polskiej"
    response_params = check_endpoint(app_tests.endpoint_create_event,event_name)
    user_token = response_params["user_token"]
    admin_token = response_params["admin_token"]

    response_params = check_endpoint(app_tests.endpoint_get_event_data,user_token)
    check_values_equal(response_params["event_name"],event_name,"endpoint_get_event_data","event_name")

    response_params = check_endpoint(app_tests.endpoint_auth_event,user_token,admin_token)
    check_values_equal(response_params["event_name"],event_name,"endpoint_auth_event","event_name")

    check_endpoint(app_tests.endpoint_delete_event,user_token,admin_token)

def test_images():
    event_name = "Tests"
    response_params = check_endpoint(app_tests.endpoint_create_event,event_name)
    user_token = response_params["user_token"]
    admin_token = response_params["admin_token"]

    response_params = check_endpoint(app_tests.endpoint_get_image_count,user_token)
    check_values_equal(int(response_params),0,"endpoint_get_image_count","")

    file_path = "./test_image.jpg"
    response_params = check_endpoint(app_tests.endpoint_add_image,user_token,file_path)
    image_id = response_params["image_id"]

    response_params = check_endpoint(app_tests.endpoint_get_image_count,user_token)
    check_values_equal(int(response_params),1,"endpoint_get_image_count","")

    response_params = check_endpoint(app_tests.endpoint_get_image_by_index,user_token,0)
    check_values_equal(response_params[0]["image_id"],image_id,"endpoint_get_image_by_index","image_id")
    with Image.open(file_path) as file:
        if file.mode != "RGB" and file.mode != "RGBA":
            raise Exception("Only RGB and RGBA files are supported.")
 
        check_values_equal(response_params[0]["width"],file.width,"endpoint_get_image_by_index","width")
        check_values_equal(response_params[0]["height"],file.height,"endpoint_get_image_by_index","height")

        pixels = bytearray(app_tests.flatten_list_of_iterables([(pixel[0],pixel[1],pixel[2],pixel[3] if len(pixel) >= 4 else 255) for pixel in file.getdata()]))
        pixel_string = base64.b64encode(pixels).decode("utf-8")
        check_values_equal(response_params[0]["pixels"],pixel_string,"endpoint_get_image_by_index","pixels")

    check_endpoint(app_tests.endpoint_delete_image,user_token,admin_token,image_id)

    response_params = check_endpoint(app_tests.endpoint_get_image_count,user_token)
    check_values_equal(int(response_params),0,"endpoint_get_image_count","")

    check_endpoint(app_tests.endpoint_delete_event,user_token,admin_token)

def test_albums_cqd():
    event_name = "Tests"
    response_params = check_endpoint(app_tests.endpoint_create_event,event_name)
    user_token = response_params["user_token"]
    admin_token = response_params["admin_token"]

    album_name = "MyAlbum"
    response_params = check_endpoint(app_tests.endpoint_create_album,user_token,album_name)
    alnum_id = response_params["album_id"]
    album_time = response_params["time"]

    response_params = check_endpoint(app_tests.endpoint_get_album_by_id,user_token,alnum_id)
    check_values_equal(response_params[0]["album_id"],alnum_id,"endpoint_get_album_by_id","album_id")
    check_values_equal(response_params[0]["name"],album_name,"endpoint_get_album_by_id","name")
    check_values_equal(response_params[0]["time"],album_time,"endpoint_get_album_by_id","time")

    check_endpoint(app_tests.endpoint_delete_album_by_id,user_token,admin_token,alnum_id)

    check_endpoint(app_tests.endpoint_delete_event,user_token,admin_token)

def test_album_images():
    event_name = "Tests"
    response_params = check_endpoint(app_tests.endpoint_create_event,event_name)
    user_token = response_params["user_token"]
    admin_token = response_params["admin_token"]

    album_name = "MyAlbum"
    response_params = check_endpoint(app_tests.endpoint_create_album,user_token,album_name)
    alnum_id = response_params["album_id"]
    album_time = response_params["time"]

    response_params = check_endpoint(app_tests.endpoint_get_album_image_count,user_token,alnum_id)
    previous_album_count = int(response_params)
    check_values_equal(previous_album_count,0,"endpoint_get_album_image_count","")

    response_params = check_endpoint(app_tests.endpoint_get_album_by_id,user_token,alnum_id)
    check_values_equal(response_params[0]["album_id"],alnum_id,"endpoint_get_album_by_id","album_id")
    check_values_equal(response_params[0]["name"],album_name,"endpoint_get_album_by_id","name")
    check_values_equal(response_params[0]["image_count"],previous_album_count,"endpoint_get_album_by_id","image_count")
    check_values_equal(response_params[0]["time"],album_time,"endpoint_get_album_by_id","time")

    file_path = "./test_image.jpg"
    response_params = check_endpoint(app_tests.endpoint_add_image,user_token,file_path,alnum_id)
    image_id = response_params["image_id"]

    response_params = check_endpoint(app_tests.endpoint_get_album_image_by_index,user_token,alnum_id,0)
    check_values_equal(response_params[0]["image_id"],image_id,"endpoint_get_album_image_by_index","image_id")

    check_endpoint(app_tests.endpoint_delete_image,user_token,admin_token,image_id)

    response_params = check_endpoint(app_tests.endpoint_get_album_image_count,user_token,alnum_id)
    check_values_equal(int(response_params),0,"endpoint_get_album_image_count","")
    check_endpoint(app_tests.endpoint_delete_album_by_id,user_token,admin_token,alnum_id)

    check_endpoint(app_tests.endpoint_delete_event,user_token,admin_token)

def try_test(test,success_counter,total_counter):
    name = test.__name__.upper()
    try:
        print(f"-------- {name} --------")
        test()
        success_counter[0] += 1
        print(f"-------- {name} SUCCEDED --------")
    except Exception as error:
        print(f"-------- {name} FAILED --------\n{error}")
    finally:
        total_counter[0] += 1

if __name__ == "__main__":
    successes = [0]
    total = [0]
    try_test(test_event_cqd,successes,total)
    try_test(test_images,successes,total)
    try_test(test_albums_cqd,successes,total)
    try_test(test_album_images,successes,total)
    print(f"-------- TESTS SUMMARY ({successes[0]}/{total[0]} = {int((successes[0] / total[0]) * 10000) / 100}%) --------")