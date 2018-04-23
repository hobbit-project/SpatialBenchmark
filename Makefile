default: build dockerize

build:
	mvn clean package -U -Dmaven.test.skip=true

dockerize:
	docker build -f spatialbenchmarkcontroller.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialbenchmarkcontroller:2.0 .
	docker build -f spatialdatagenerator.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialdatagenerator:2.0 .
	docker build -f spatialtaskgenerator.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialtaskgenerator:2.0 .
	docker build -f spatialevaluationmodule.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialevaluationmodule:2.0 .
	docker build -f limessystemadapter.docker -t git.project-hobbit.eu:4567/jsaveta1/limessystemadapter .
	docker build -f silksystemadapter.docker -t git.project-hobbit.eu:4567/jsaveta1/silksystemadapter .
	docker build -f strabonsystemadapter.docker -t git.project-hobbit.eu:4567/jsaveta1/strabonsystemadapter .

	docker push git.project-hobbit.eu:4567/jsaveta1/spatialbenchmarkcontroller:2.0
	docker push git.project-hobbit.eu:4567/jsaveta1/spatialdatagenerator:2.0
	docker push git.project-hobbit.eu:4567/jsaveta1/spatialtaskgenerator:2.0
	docker push git.project-hobbit.eu:4567/jsaveta1/spatialevaluationmodule:2.0
	docker push git.project-hobbit.eu:4567/jsaveta1/limessystemadapter
	docker push git.project-hobbit.eu:4567/jsaveta1/silksystemadapter
	docker push git.project-hobbit.eu:4567/jsaveta1/strabonsystemadapter
	
	
