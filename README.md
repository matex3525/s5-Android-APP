Aby odpalić, wymagany docker

z /Flask backend wykonać:
docker compose up --build



Nginx (jeszcze) nie ma
![1](images/Pasted image 20241005230756.png)
![2](images/Pasted image 20241007183707.png)

API:
### ``/event``
#### POST
Parameterless request to generate a new event - users need this name.

**RETURNs:**
- 201
```json
{  
	"event:name": "newly generated event"  
}
```


### ``/images/{event_name}``
#### POST
Takes a json, with  .jpg encoded in b64
```json
{
	"b64": "a b64 encoded jpg"
}
```
**RETURNs:**
- 201
```json
{
	"image_id": "image_id"
}
```

- "Event does not exist", 400
- "b64 not in request", 400

### ``/images/{event_name}/{image_id}``
#### GET
Get images that are newer then specified image_id.
If from the start, put in "-"

**RETURNs:**
- 200
```json
{
	"images": [
		{
			"image_id": "image_id1",
			"b64": "b64 encoded image"
		},
		{
			"image_id": "image_id2",
			"b64": "b64 encoded image"
		},
	]
}
```
- "Event does not exist", 400


przy 112 zdjęciach jednym requestem (nigdy albo tylko raz będzie tak duży) radzi sobie swietnie.
![3](images/Pasted image 20241008224437.png)