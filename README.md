# NSK – wskaźniki niezawodności struktur

Aplikacja JavaFX wyznaczająca wskaźniki niezawodności obiektów nienaprawialnych:
funkcję niezawodności **R(t)**, funkcję zawodności **F(t)** i średni czas poprawnej pracy **ET**
dla struktury **szeregowej**, **równoległej** i **k-z-N**.

Projekt laboratoryjny z przedmiotu *Niezawodność Systemów Komputerowych*, Politechnika Świętokrzyska.

## Wymagania

- JDK 21
- Maven 3.9+

## Uruchomienie

```bash
mvn javafx:run
```

Testy:

```bash
mvn test
```

## Dane wejściowe

| Dana | Opis | Zakres |
|---|---|---|
| Struktura | szeregowa / równoległa / k-z-N | – |
| N | liczba elementów | 1 ≤ N ≤ 1000 |
| k | liczba elementów, które muszą być sprawne (tylko k-z-N) | 1 ≤ k ≤ N |
| t | chwila czasu [h] | t ≥ 0 |
| λ | intensywność uszkodzeń elementu [1/h] | λ > 0 |

## Status

- [x] Formularz i walidacja danych
- [ ] Obliczenia R(t), F(t), ET
- [ ] Wersja wykonywalna (.exe)

## Literatura

M. Prażewska (red.), *Niezawodność urządzeń elektronicznych*, WKiŁ, Warszawa 1987, str. 355.
