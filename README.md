# Secure Credential Management

This project deals with all issues surrounding the secure management of credentials in web based application environments.

Our principal intention is to make a thourough analisys of the architecture of common web based application, address weaknesses that can lead to the leakage of credentials and work on components that can be reused to reduce the vulnerability of to applications.

## Setup

### <a name="xs2a"></a>Running xs2a

With docker installed it is easy.
Just run...

```bash
docker run -it -v $PWD:/usr/src/app -p 8090:8080 adorsys/openjdk-build-base /usr/src/app/scripts/runxs2a.sh
```

This might take some time. If everything is fine you find your documented API [here](http://localhost:8090/swagger-ui/?url=http://localhost:8090/swagger.json)
