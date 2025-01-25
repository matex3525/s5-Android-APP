## Jak uruchomić serwer?
```
docker compose up --build
```

## Dokumentacja API
Odpowiedź z serwera zawsze jest postaci:
```json
{"success": /*true lub false*/,"params": /*jakieś dane*/}
```
Jeśli ``"success"`` = true, ``"params"`` zawiera dodatkowe informacje związwne z zapytaniem.<br>
Jeśli ``"success"`` = false, ``"params"`` zawiera kod błędu:<br>
<pre style="font-family: inherit">
0 - nazwa wydarzenia jest niepoprawna
1 - token admina jest niepoprawny
2 - string podany przez użytkownika jest zbyt długi
3 - string podany przez użytkownika nie przeszedł przez filtr (zawierał nieodpowiedni język lub zawierał treści, które przy wyświetleniu powodowały by problemy)
4 - błąd wewnętrzny (serwer rzucił wyjątkiem podczas obsługi żądania, niepoprawny format zapytania, próba odwołania się do nieistniejącego zasobu lub inny błąd)
5 - Niepoprawna szerokość obrazka
6 - Niepoprawna wysokość obrazka
7 - Niepoprawny indeks obrazka
8 - Niepoprawny indeks komentarza
9 - Niepoprawne ID albumu
10 - Niepoprawny indeks albumu
</pre>

### ``/v0/event`` (POST)
Generuje nowe wydarzenie.<br>
#### Przyjmuje
```json
{"event_name": "Nazwa wydarzenia"}
```
#### Zwraca
```json
{"success": true,"params": {"user_token": "string","admin_token": "string"}}
```

### ``/v0/event/<user_token>`` (DELETE)
Usuwa wydarzenie &lt;user_token&gt;.
#### Przyjmuje
```json
{"admin_token": "token admina"}
```
#### Zwraca
```json
{"success": true,"params": {}}
```

### ``/v0/event/<user_token>`` (GET)
Zwraca informacje o wydarzeniu &lt;user_token&gt;.
#### Zwraca
```json
{"success": true,"params": {"event_name": "nazwa wydarzenia"}}
```

### ``/v0/event/<user_token>/check`` (POST)
Sprawdza poprawność tokenu admina dla wydarzenia &lt;user_token&gt;.
#### Przyjmuje
```json
{"admin_token": "token admina"}
```
#### Zwraca
```json
{"success": true,"params": {"event_name": "nazwa wydarzenia"}}
```

## Endpointy do zdjęć

### ``/v0/event/<user_token>/imagecount`` (GET)
Zwraca ilość zdjęć przypisanych do wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{"success": true,"params": /*ilość zdjęć*/}
```

### ``/v0/event/<user_token>/imageids/<first_index>/<last_index>`` (GET)
Zwraca ID zdjęć przypisanych do wydarzenia &lt;user_token&gt; o indeksach od &lt;first_index&gt; do &lt;last_index&gt; (włącznie).
#### Zwraca
```json
{"success": true,"params": ["ID0","ID1",/*...*/]}
```

### ``/v0/event/<user_token>/image`` (POST)
Dodaje zdjęcie do wydarzenia &lt;user_token&gt;.
#### Przyjmuje
```json
{
	"width": /*szerokość zdjęcia w pikselach*/,
	"height": /*wysokość zdjęcia w pikselach*/,
	"description": "opis zdjęcia",
	"pixels": "piksele zdjęcia w formacie ARGB8888 zakodowane w Base64"
}
```
lub
```json
{
	"width": /*szerokość zdjęcia w pikselach*/,
	"height": /*wysokość zdjęcia w pikselach*/,
	"description": "opis zdjęcia",
	"pixels": "piksele zdjęcia w formacie ARGB8888 zakodowane w Base64",
	"album_id": "ID albumu, do którego przypisać zdjęcie"
}
```
#### Zwraca
```json
{"success": true,"params": {"image_id": "ID zdjęcia"}}
```

### ``/v0/event/<user_token>/image/byid/<image_id>`` (DELETE)
Usuwa zdjęcie o ID &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.
#### Przyjmuje
```json
{"admin_token": "token admina"}
```
#### Zwraca
```json
{"success": true,"params": {}}
```

### ``/v0/event/<user_token>/image/byindex/<image_index>`` (GET)
Zwraca dane zdjęcia o indeksie &lt;image_index&gt; z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": [{
		"image_id": "ID zdjęcia",
		"width": /*szerokość zdjęcia w pikselach*/,
		"height": /*wysokość zdjęcia w pikselach*/,
		"description": "opis zdjęcia",
		"pixels": "piksele zdjęcia w formacie ARGB8888 zakodowane w Base64"
	}]
}
```

### ``/v0/event/<user_token>/image/byid/<image_id>`` (GET)
Zwraca dane zdjęcia o ID &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": [{
		"image_id": "ID zdjęcia",
		"width": /*szerokość zdjęcia w pikselach*/,
		"height": /*wysokość zdjęcia w pikselach*/,
		"description": "opis zdjęcia",
		"pixels": "piksele zdjęcia w formacie ARGB8888 zakodowane w Base64"
	}]
}
```

### ``/v0/event/<user_token>/image/byindices/<first_image_index>/<last_image_index>`` (GET)
Zwraca dane zdjęć o indeksach od &lt;first_image_index&gt; do &lt;last_image_index&gt; (włączenie) z wydarzenia &lt;user_token&gt;.<br>
Jeśli `first_image_index == 0` oraz `last_image_index == -1`, to zwraca wszystkie zdjęcia.
#### Zwraca
```json
{
	"success": true,
	"params": [
		{
			"image_id": "ID zdjęcia 1",
			"width": /*szerokość zdjęcia w pikselach*/,
			"height": /*wysokość zdjęcia w pikselach*/,
			"description": "opis zdjęcia",
			"pixels": "piksele zdjęcia w formacie ARGB8888 zakodowane w Base64"
		},
		{
			"image_id": "ID zdjęcia 2",
			"width": /*szerokość zdjęcia w pikselach*/,
			"height": /*wysokość zdjęcia w pikselach*/,
			"description": "opis zdjęcia",
			"pixels": "piksele zdjęcia w formacie ARGB8888 zakodowane w Base64"
		},
		/*...*/
	]
}
```

## Endpointy do ikon (zmniejszonych wersji zdjęć, które służą do wyświetlania ich na podglądach lub w sytuacjach, w których pełna jakość nie jest wymagana)

### ``/v0/event/<user_token>/imagethumbs/byindex/<image_index>`` (GET)
Zwraca dane ikony o indeksie &lt;image_index&gt; z wydarzenia &lt;user_token&gt;.<br>
#### Zwraca
```json
{
	"success": true,
	"params": [{
		"image_id": "ID zdjęcia, do którego należy ikona",
		"width": /*szerokość ikony w pikselach*/,
		"height": /*wysokość ikony w pikselach*/,
		"description": "opis zdjęcia, do którego należy ikona",
		"pixels": "piksele ikony w formacie ARGB8888 zakodowane w Base64"
	}]
}
```

### ``/v0/event/<user_token>/imagethumbs/byid/<image_id>`` (GET)
Zwraca dane ikony o ID &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.<br>
#### Zwraca
```json
{
	"success": true,
	"params": [{
		"image_id": "ID zdjęcia, do którego należy ikona",
		"width": /*szerokość ikony w pikselach*/,
		"height": /*wysokość ikony w pikselach*/,
		"description": "opis zdjęcia, do którego należy ikona",
		"pixels": "piksele ikony w formacie ARGB8888 zakodowane w Base64"
	}]
}
```

### ``/v0/event/<user_token>/imagethumbs/byindices/<first_image_index>/<last_image_index>`` (GET)
Zwraca dane ikon o indeksach od &lt;first_image_index&gt; do &lt;last_image_index&gt; (włączenie) z wydarzenia &lt;user_token&gt;.<br>
Jeśli `first_image_index == 0` oraz `last_image_index == -1`, to zwraca wszystkie ikony.
#### Zwraca
```json
{
	"success": true,
	"params": [
		{
			"image_id": "ID zdjęcia, do którego należy ikona 1",
			"width": /*szerokość ikony w pikselach*/,
			"height": /*wysokość ikony w pikselach*/,
			"description": "opis zdjęcia, do którego należy ikona",
			"pixels": "piksele ikony w formacie ARGB8888 zakodowane w Base64"
		},
		{
			"image_id": "ID zdjęcia, do którego należy ikona 2",
			"width": /*szerokość ikony w pikselach*/,
			"height": /*wysokość ikony w pikselach*/,
			"description": "opis zdjęcia, do którego należy ikona",
			"pixels": "piksele ikony w formacie ARGB8888 zakodowane w Base64"
		},
		/*...*/
	]
}
```

## Endpointy do komentarzy

### ``/v0/event/<user_token>/image/byid/<image_id>/comment`` (POST)
Dodaj komentarz do zdjęcia o ID &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.
#### Przyjmuje
```json
{"text": "Treść komentarza"}
```
#### Zwraca
```json
{"success": true,"params": {"comment_id": "ID komentarza","time": /*Czas dodania komentarza w milisekundach od 1 stycznia 1970 r.*/}}
```

### ``/v0/event/<user_token>/image/byid/<image_id>/commentcount`` (GET)
Zwraca ilość komentarzy przypisanych dp zdjęcia o ID &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{"success": true,"params": /*ilość komentarzy*/}
```

### ``/v0/event/<user_token>/image/byid/<image_id>/commentids/<first_index>/<last_index>`` (GET)
Zwraca ID komentarzy przypisanych do zdjęcia o ID &lt;image_id&gt; z wydarzenia &lt;user_token&gt; o indeksach od &lt;first_index&gt; do &lt;last_index&gt; (włącznie).
#### Zwraca
```json
{"success": true,"params": ["ID0","ID1",/*...*/]}
```

### ``/v0/event/<user_token>/image/byid/<image_id>/comment/byid/<comment_id>`` (DELETE)
Usuwa komentarz o ID &lt;comment_id&gt; przypisany do zdjęcia &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.
#### Przyjmuje
```json
{"admin_token": "token admina"}
```
#### Zwraca
```json
{"success": true,"params": {}}
```

### ``/v0/event/<user_token>/image/byid/<image_id>/comment/byindex/<comment_index>`` (GET)
Zwraca dane komentarza o indeksie &lt;comment_index&gt; przypisanego do zdjęcia &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": [{
		"comment_id": "ID komentarza",
		"text": "Treść komentarza",
		"time": /*Czas dodania komentarza w milisekundach od 1 stycznia 1970 r.*/
	}]
}
```

### ``/v0/event/<user_token>/image/byid/<image_id>/comment/byid/<comment_id>`` (GET)
Zwraca dane komentarza o ID &lt;comment_id&gt; przypisanego do zdjęcia &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": [{
		"comment_id": "ID komentarza",
		"text": "Treść komentarza",
		"time": /*Czas dodania komentarza w milisekundach od 1 stycznia 1970 r.*/
	}]
}
```

### ``/v0/event/<user_token>/image/byid/<image_id>/comment/byindices/<first_comment_index>/<last_comment_index>`` (GET)
Zwraca dane komentarzy o indeksach od &lt;first_comment_index&gt; do &lt;last_comment_index&gt; (włączenie) przypisanych do zdjęcia &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.<br>
Jeśli `first_comment_index == 0` oraz `last_comment_index == -1`, to zwraca wszystkie komentarze.
#### Zwraca
```json
{
	"success": true,
	"params": [
		{
			"comment_id": "ID komentarza 1",
			"text": "Treść komentarza",
			"time": /*Czas dodania komentarza w milisekundach od 1 stycznia 1970 r.*/
		},
		{
			"comment_id": "ID komentarza 2",
			"text": "Treść komentarza",
			"time": /*Czas dodania komentarza w milisekundach od 1 stycznia 1970 r.*/
		},
		/*...*/
	]
}
```

## Endpointy do albumów (folderów na zdjęcia)

### ``/v0/event/<user_token>/album`` (POST)
Dodaje album do wydarzenia &lt;user_token&gt;.
#### Przyjmuje
```json
{"name": "Nazwa albumu"}
```
#### Zwraca
```json
{
	"success": true,
	"params": {"album_id": "ID albumu","time": /*Czas dodania albumu w milisekundach od 1 stycznia 1970 r.*/}
}
```

### ``/v0/event/<user_token>/album/byid/<album_id>`` (DELETE)
Usuwa album o ID &lt;album_id&gt; z wydarzenia &lt;user_token&gt;.
#### Przyjmuje
```json
{"admin_token": "token admina"}
```
#### Zwraca
```json
{"success": true,"params": {}}
```

### ``/v0/event/<user_token>/album/byindices/<first_album_index>/<last_album_index>`` (GET)
Zwraca listę albumów o indeksach od &lt;first_album_index&gt; do &lt;last_album_index&gt; (włączenie) z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": [
		{
            "album_id": "ID albumu 1",
            "name": "Nazwa albumu 1",
			"image_count": /*Ilość zdjęć należących do albumu 1*/,
            "time": /*Czas dodania albumu 1 w milisekundach od 1 stycznia 1970 r.*/
        },
		{
            "album_id": "ID albumu 2",
            "name": "Nazwa albumu 2",
			"image_count": /*Ilość zdjęć należących do albumu 2*/,
            "time": /*Czas dodania albumu 2 w milisekundach od 1 stycznia 1970 r.*/
        },
		...
	]
}
```

### ``/v0/event/<user_token>/album/byindex/<album_index>`` (GET)
Zwraca album o indeksie &lt;album_index&gt; z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": [{
		"album_id": "ID albumu",
		"name": "Nazwa albumu",
		"image_count": /*Ilość zdjęć należących do albumu*/,
		"time": /*Czas dodania albumu w milisekundach od 1 stycznia 1970 r.*/
	}]
}
```

### ``/v0/event/<user_token>/album/byid/<album_id>`` (GET)
Zwraca album o ID &lt;album_id&gt; z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": [{
		"album_id": "ID albumu",
		"name": "Nazwa albumu",
		"image_count": /*Ilość zdjęć należących do albumu*/,
		"time": /*Czas dodania albumu w milisekundach od 1 stycznia 1970 r.*/
	}]
}
```

### ``/v0/event/<user_token>/album/byid/<album_id>/imageids/<first_image_index>/<last_image_index>`` (GET)
Zwraca ID zdjęć przypisanych do albumu &lt;album_id&gt; o indeksach od &lt;first_image_index&gt; do &lt;last_image_index&gt; (włączenie) z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": ["ID zdjęcia 1","ID zdjęcia 2",...]
}
```

### ``/v0/event/<user_token>/album/byid/<album_id>/imagecount`` (GET)
Zwraca ilość zdjęć przypisanych do albumu &lt;album_id&gt; z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": /*Ilość zdjęć*/
}
```

### ``/v0/event/<user_token>/album/byid/<album_id>/image/byindex/<image_index>`` (GET)
Zwraca dane zdjęcia o indeksie &lt;image_index&gt; przypisanego do albumu &lt;album_id&gt; z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": [{
		"image_id": "ID zdjęcia",
		"width": /*szerokość zdjęcia w pikselach*/,
		"height": /*wysokość zdjęcia w pikselach*/,
		"description": "opis zdjęcia",
		"pixels": "piksele zdjęcia w formacie ARGB8888 zakodowane w Base64"
	}]
}
```

### ``/v0/event/<user_token>/album/byid/<album_id>/imagethumbs/byindex/<image_index>`` (GET)
Zwraca dane ikony o indeksie &lt;image_index&gt; przypisanej do albumu &lt;album_id&gt; z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{
	"success": true,
	"params": [{
		"image_id": "ID zdjęcia",
		"width": /*szerokość ikony w pikselach*/,
		"height": /*wysokość ikony w pikselach*/,
		"description": "opis ikony",
		"pixels": "piksele ikony w formacie ARGB8888 zakodowane w Base64"
	}]
}
```