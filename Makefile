MODULE = db
VERSION = 1.0.0

GEN = gen

SRC_PROD = src/prod
SRC_TEST = src/test
SRC_DEMO = src/demo

CLS_PROD = gen/classes/prod
CLS_TEST = gen/classes/test
CLS_DEMO = gen/classes/demo

CP_BASE = lib/run/\*:lib/test/\*
CP_PROD = ${CP_BASE}:${CLS_PROD}
CP_TEST = ${CP_PROD}:${CLS_TEST}

DIST = ${GEN}/dist

JAR = ${DIST}/${MODULE}.jar
JAR_SRC = ${DIST}/${MODULE}-src.jar

MANIFEST = etc/MANIFEST.MF
DIST_MANIFEST = ${GEN}/MANIFEST.MF

DIRECTORIES = ${GEN} ${GEN}/tmp ${CLS_DEMO} ${CLS_PROD} ${CLS_TEST} ${DIST}

.PHONY: clean dist compile size repl 

default: test dist

compile: clean ${CLS_PROD} ${CLS_TEST} ${CLS_DEMO}
	find ${SRC_PROD} -name "*.scala" | xargs -s 30000 scalac -classpath ${CP_BASE} -d ${CLS_PROD}
	find ${SRC_DEMO} -name "*.scala" | xargs -s 30000 scalac -classpath ${CP_PROD} -d ${CLS_DEMO}
	find ${SRC_TEST} -name "*.scala" | xargs -s 30000 scalac -classpath ${CP_PROD} -d ${CLS_TEST} 

test: compile
	scala -cp ${CP_TEST} org.scalatest.tools.Runner -p ${CLS_TEST} -oDFW 

${JAR}: compile ${DIST_MANIFEST} ${DIST}
	jar cfm ${JAR} ${DIST_MANIFEST} -C ${CLS_PROD} .

${JAR_SRC}: ${DIST}
	jar cf ${JAR_SRC} -C ${SRC_PROD} .

dist: clean ${TAR}

${DIST_MANIFEST}: ${GEN}
	sed -e 's/VERSION/${VERSION}/' ${MANIFEST} > ${DIST_MANIFEST}

repl: compile
	scala -classpath ${CP_BASE}:${CLS_PROD}:${CLS_TEST}

size: 
	find ${SRC_PROD} -name "*.scala" | xargs wc | sort -n

${DIRECTORIES}:
	mkdir -p $@

clean:
	rm -rf ${GEN}; find . -name "*~" -o -name "*.core" -print0 | xargs -0 rm -f
