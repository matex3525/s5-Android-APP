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
{"success": true,"params": {}}
```

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
Dodaj zdjęcie do wydarzenia &lt;user_token&gt;.
#### Przyjmuje
```json
{
	"width": /*szerokość zdjęcia w pikselach*/,
	"height": /*wysokość zdjęcia w pikselach*/,
	"description": "opis zdjęcia",
	"pixels": "piksele zdjęcia w formacie ARGB8888 zakodowane w Base64"
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

### ``/v0/event/<user_token>/image/byid/<image_id>/comment`` (POST)
Dodaj komentarz do zdjęcia o ID &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.
#### Przyjmuje
```json
{"text": "Treść komentarza"}
```
#### Zwraca
```json
{"success": true,"params": {"comment_id": "ID komentarza"}}
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
		"time": /*Czas dodania komentarza w nanosekundach od 1 stycznia 1970 r.*/
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
		"time": /*Czas dodania komentarza w nanosekundach od 1 stycznia 1970 r.*/
	}]
}
```