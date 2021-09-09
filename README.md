# b2w-starwars-api
B2W Star Wars API

Pontos de atenção:
- Ao cadastrar um planeta, o nome é validado através da SWAPI, permitindo apenas nomes de planetas válidos, de acordo aos filmes.
- A quantidade de aparições nos filmes é consultada ao criar o planeta e fica salvo na collection local.
- Foi feito um endpoint para atualizar a quantidade de aparições nos filmes consultando novamente a SWAPI para todos os registros gravados localmente.
 PATCH : /api/planets/update/filmsAppearance
- Para rodar os testes de integração é necessário ter o Docker instalado na máquina. Por causa do uso do TestContainers.
- Há um relatório de cobertura de testes na pasta test-coverage-report, arquivo index.html.