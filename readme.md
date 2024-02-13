## Be Fit
Keep track of your calorie intake to help you stay fit.

### Description

### Modules
- [db-schema](./db-schema/readme.md): `db-schema` contains the database schema for the application.
- [api](./api/readme.md): `api` contains the RESTful API for the application.
- [db-loader](./db-loader/readme.md): `db-loader` contains the database loader for the application.

### Local Development
#### Tools required
- [Scala](https://www.scala-lang.org/download/) (v2.13)
- [PostgreSQL](https://www.postgresql.org/download/) (v12)

#### Pre-requisites
1. Create a database in PostgreSQL. (Using the configuration mentioned in `application.conf` of `db-schema` module)
2. Download raw data from [USDA](https://fdc.nal.usda.gov/download-datasets.html)
3. Unzip the file and place it in the `resources` directory of `db-loader` module.
4. Run the `db-loader` module to load the data into the database.
5. Once the data is loaded, run the `api` module to start the server.
6. The server will start at `http://localhost:8080`. You can now use the API to interact with the application.
You can import [insomnia.json](./docs/insomnia.json) into [Insomnia](https://insomnia.rest/download/) to test the API.

