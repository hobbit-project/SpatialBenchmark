default: build dockerize

build:
	mvn clean package -U -Dmaven.test.skip=true

dockerize:
	docker build -f spatialbenchmarkcontroller.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialbenchmarkcontroller .
	docker build -f spatialdatagenerator.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialdatagenerator .
	docker build -f spatialtaskgenerator.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialtaskgenerator .
	docker build -f spatialevaluationmodule.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialevaluationmodule .
	docker build -f limessystemadapter.docker -t git.project-hobbit.eu:4567/jsaveta1/limessystemadapter .

	docker push git.project-hobbit.eu:4567/jsaveta1/spatialbenchmarkcontroller
	docker push git.project-hobbit.eu:4567/jsaveta1/spatialdatagenerator
	docker push git.project-hobbit.eu:4567/jsaveta1/spatialtaskgenerator
	docker push git.project-hobbit.eu:4567/jsaveta1/spatialevaluationmodule
	docker push git.project-hobbit.eu:4567/jsaveta1/limessystemadapter
	
	