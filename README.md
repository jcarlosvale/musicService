# Welcome to the Music Service API

This project contains a REST API for providing clients with information about a specific music artist. The information is collected from 4 different
sources: MusicBrainz, Wikidata, Wikipedia and Cover Art Archive

## API Documentation:
First start the application and visit:
http://localhost:8080/swagger-ui/index.html

### Endpoint

_GET /artist/{mbid}_

Example:
http://localhost:8080/artist/e7273fcd-7530-48ba-bd11-1879f4a8920f

### Some IDs
* Michael Jackson: f27ec8db-af05-4f36-916e-3d57f91ecf5e
* Carach Angren: e7273fcd-7530-48ba-bd11-1879f4a8920f
* other ids: https://musicbrainz.org/

### Example output
```json

  "id": "e7273fcd-7530-48ba-bd11-1879f4a8920f",
  "name": "Carach Angren",
  "gender": null,
  "country": "NL",
  "disambiguation": "",
  "description": "<p><b>Carach Angren</b> is a symphonic black metal band from the Netherlands, formed by two members of the now-defunct bands Inger Indolia and Vaultage. Their style is characterized by prominent usage of orchestral arrangements, death metal vocals, and guitar riffs. Their studio albums are concept albums with lyrics based on ghost stories and folklore, such as the <i>Flying Dutchman</i>. The band sets itself apart from other symphonic black metal artists with the usage of multiple languages such as English, French, German, and Dutch. Most songs are based on English, with transitions into other languages during chorus and bridge sections.</p>",
  "albums": [
    {
      "id": "23c9abc4-5bd7-3e5e-b995-f7946f930f63",
      "title": "Lammendam",
      "imageUrl": "http://coverartarchive.org/release/99a6d217-c3cf-498a-9d2c-e7bd89ff93da/10038719708.jpg"
    },
    {
      "id": "50acfaab-65c6-4c06-8d33-d2fe5c1021d3",
      "title": "Death Came Through a Phantom Ship",
      "imageUrl": "http://coverartarchive.org/release/4c6afd7a-ee19-4f46-8328-5267525c2d16/9732288219.jpg"
    },
    {
      "id": "d722938c-ba46-4b9b-a7f8-05caa275f5e0",
      "title": "Where the Corpses Sink Forever",
      "imageUrl": "http://coverartarchive.org/release/3a0257bf-31f0-4a3e-9918-7b29ef934132/10038712814.jpg"
    },
    {
      "id": "72b3bf72-c783-4afc-88ee-56c510c47632",
      "title": "This Is No Fairytale",
      "imageUrl": "http://coverartarchive.org/release/36753db7-a19f-4477-84ae-58da939c8df5/12900472032.jpg"
    },
    {
      "id": "42eb4a27-4bad-4eed-9c88-df2141f9f7e3",
      "title": "Dance and Laugh Amongst the Rotten",
      "imageUrl": "http://coverartarchive.org/release/2ed101a8-5772-4786-8069-9fe13b5e2914/17011914824.jpg"
    },
    {
      "id": "23b2a103-b986-42b3-97e9-6de6351f5de0",
      "title": "Franckensteina Strataemontanus",
      "imageUrl": "http://coverartarchive.org/release/d57c518e-7814-4686-93a0-b03ac6a3cc67/30448314386.jpg"
    }
  ]
}
```

## Technology

* Maven
* Java 11
* SpringBoot 2.X
* JUnit 5 - Unit and Integration tests
* Caffeine (Cache)
* Resilience4J
* OpenAPI 3.0

## Commands:

To run:

    mvn spring-boot:run

To compile, test:

    mvn clean install