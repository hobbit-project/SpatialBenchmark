default: build dockerize

build:
	mvn clean package -U -Dmaven.test.skip=true

dockerize:
	docker build -f docker/versioningbenchmarkcontroller.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialbenchmarkcontroller .
	docker build -f docker/versioningdatagenerator.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialdatagenerator .
	docker build -f docker/versioningtaskgenerator.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialtaskgenerator .
	docker build -f docker/versioningevaluationmodule.docker -t git.project-hobbit.eu:4567/jsaveta1/spatialevaluationmodule .
	docker build -f docker/versioningvirtuososystemadapter.docker -t git.project-hobbit.eu:4567/jsaveta1/limessystemadapter .

	docker push git.project-hobbit.eu:4567/jsaveta1/spatialbenchmarkcontroller
	docker push git.project-hobbit.eu:4567/jsaveta1/spatialdatagenerator
	docker push git.project-hobbit.eu:4567/jsaveta1/spatialtaskgenerator
	docker push git.project-hobbit.eu:4567/jsaveta1/spatialevaluationmodule
	docker push git.project-hobbit.eu:4567/jsaveta1/limessystemadapter
	
	