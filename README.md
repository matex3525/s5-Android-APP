## Jak uruchomić serwer?
```
docker compose up --build
```

## "Dokumentacja" API
Odpowiedź z serwera zawsze jest postaci:
```json
{"success": /*0 or 1*/,"params": /*object*/}
```
Jeśli ``"success"`` zawiera 1, ``"params"`` zawiera dodatkowe informacje.<br>
Jeśli ``"success"`` zawiera 0, ``"params"`` zawiera kod błędu:<br>
> 0 - nazwa wydarzenia jest niepoprawna<br>
1 - token admina jest niepoprawny<br>
2 - string podany przez użytkownika jest zbyt długi<br>
3 - string podany przez użytkownika nie przeszedł przez filtr<br>
4 - błąd wewnętrzny

### ``/event`` (POST)
Generuje nowe wydarzenie.<br>
#### Przyjmuje
```json
{"event_name": "Nazwa wydarzenia"}
```
#### Zwraca
```json
{"success": 1,"params": {"user_token": "string","admin_token": "string"}}
```
#### Zwraca kody błędu: 2,3 oraz 4

### ``/event/<user_token>`` (GET)
Sprawdza czy wydarzenie &lt;user_token&gt; istnieje.
#### Zwraca
```json
{"success": 1,"params": /*0 lub 1*/}
```
#### Zwraca kody błędu: 4

### ``/event/<user_token>`` (DELETE)
Usuwa wydarzenie &lt;user_token&gt;.
#### Przyjmuje
```json
{"admin_token": "Token admina"}
```
#### Zwraca
```json
{"success": 1,"params": null}
```
#### Zwraca kody błędu: 0,1 oraz 4

### ``/images/<user_token>`` (POST)
Dodaje obraz do wydarzenia.
#### Przyjmuje
```json
{"b64": "Base64 obrazka","title": "Nazwa obrazka"}
```
#### Zwraca
```json
{"success": 1,"params": {"image_id": "ID"}}
```
#### Zwraca kody błędu: 0,2,3 oraz 4

### ``/images/<user_token>/<image_id>`` (GET)
Zwraca obraz o ID &lt;image_id&gt; z wydarzenia &lt;user_token&gt;.
Jeśli &lt;image_id&gt; = "0-0", zwraca wszystkie obrazki.
#### Zwraca
```json
{
	"success": 1,
	"params": [
		{
			"image_id": "ID",
			"b64": "Base64",
			"title": "Tytuł"
		},
		/*...*/
	]
}
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
{"success": 1,"params": null}
```
#### Zwraca kody błędu: 0,1 oraz 4