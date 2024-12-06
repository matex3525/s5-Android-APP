## Jak uruchomić serwer?
```
docker compose up --build
```

## "Dokumentacja" API
Odpowiedź z serwera zawsze jest postaci:
```json
{"success": /*false or true*/,"params": /*object*/}
```
Jeśli ``"success"`` = true, ``"params"`` zawiera dodatkowe informacje.<br>
Jeśli ``"success"`` = false, ``"params"`` zawiera kod błędu:<br>
> 0 - nazwa wydarzenia jest niepoprawna<br>
1 - token admina jest niepoprawny<br>
2 - string podany przez użytkownika jest zbyt długi<br>
3 - string podany przez użytkownika nie przeszedł przez filtr<br>
4 - błąd wewnętrzny<br>
5 - Niepoprawna szerokość obrazka<br>
6 - Niepoprawna wysokość obrazka

### ``/event`` (POST)
Generuje nowe wydarzenie.<br>
#### Przyjmuje
```json
{"event_name": "Nazwa wydarzenia"}
```
#### Zwraca
```json
{"success": true,"params": {"user_token": "string","admin_token": "string"}}
```
#### Zwraca kody błędu: 2,3 oraz 4

### ``/event/<user_token>`` (GET)
Sprawdza czy wydarzenie &lt;user_token&gt; istnieje.
#### Zwraca
```json
{"success": true,"params": {"event_name": "nazwa"}}
```
#### Zwraca kody błędu: 0 oraz 4.

### ``/event/<user_token>`` (DELETE)
Usuwa wydarzenie &lt;user_token&gt;.
#### Przyjmuje
```json
{"admin_token": "Token admina"}
```
#### Zwraca
```json
{"success": true,"params": {}}
```
#### Zwraca kody błędu: 0,1 oraz 4

### ``/auth/<user_token>`` (POST)
Sprawdza czy wydarzenie istnieje oraz sprawdza też poprawność tokenu admina.
#### Przyjmuje (opcjonalnie)
```json
{"admin_token": "Token admina"}
```
#### Zwraca
```json
{"success": true,"params": {}}
```
#### Zwraca kody błędu: 0 oraz 1.

### ``/images/<user_token>`` (POST)
Dodaje obraz do wydarzenia.
#### Przyjmuje
```json
{"width": /*szerokość*/,"height": /*długość*/,"pixels": "piksele","title": "Nazwa obrazka"}
```
#### Zwraca
```json
{"success": true,"params": {"image_id": "ID"}}
```
#### Zwraca kody błędu: 0,2,3,4,5 oraz 6.

### ``/images/<user_token>/<image_id>`` (GET)
Zwraca obraz o ID &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.
Jeśli &lt;image_id&gt; = "0-0", zwraca wszystkie obrazki.
#### Zwraca
```json
{
	"success": true,
	"params": [
		{
			"image_id": "ID",
			"width": /*szerokość*/,
			"height": /*długość*/,
			"pixels": "piksele",
			"title": "Tytuł"
		},
		/*...*/
	]
}
```
#### Zwraca kody błędu: 0 oraz 4

### ``/getimagecount/<user_token>`` (GET)
Zwraca ilość obrazów z wydarzenia &lt;user_token&gt;.
#### Zwraca
```json
{"success": true,"params": /*ilość*/}
```
#### Zwraca kody błędu: 0 oraz 4

### ``/images/<user_token>/<image_id>`` (DELETE)
Usuwa obraz o ID &lt;image_id&gt; w wydarzeniu &lt;user_token&gt;.
#### Przyjmuje
```json
{"admin_token": "Token admina"}
```
#### Zwraca
```json
{"success": true,"params": {}}
```
#### Zwraca kody błędu: 0,1 oraz 4