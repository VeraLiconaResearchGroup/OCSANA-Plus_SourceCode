.PHONY: build build-fast build-fast03 build-standard clean install

MAKE=make

all: build
	mkdir -p bin
	cp src/featurize src/buildmodel src/scoreit bin
	@echo ------------------------------------------------------------------------------
	@echo FEATURE has successfully been built and tested.
	@echo ------------------------------------------------------------------------------

clean:
	rm -rf bin
	cd src && $(MAKE) clean
	cd tests && $(MAKE) clean

build:
	- \
	$(MAKE) build-fast     || \
	$(MAKE) build-fast03   || \
	$(MAKE) build-standard

build-fast:
	@echo ==============================================================================
	@echo Attempting to build the fast version of FEATURE \(requires C++11\)
	@echo ==============================================================================
	$(MAKE) clean
	cd src && $(MAKE) fast
	@echo Testing Fast C++11 FEATURE
	cd tests && $(MAKE)
	@echo ==============================================================================
	@echo SUCCESS! Fast FEATURE \(C++11\) has been compiled and tested.
	@echo ==============================================================================

build-fast03:
	@echo Fast C++11 FEATURE didn\'t work.
	@echo ==============================================================================
	@echo Attempting to build the fast version of FEATURE \(requires C++03\)
	@echo ==============================================================================
	$(MAKE) clean
	cd src && $(MAKE) fast03
	@echo Testing Fast C++03 FEATURE
	cd tests && $(MAKE)
	@echo ==============================================================================
	@echo SUCCESS! Fast FEATURE \(C++03\) has been compiled and tested.
	@echo ==============================================================================

build-standard:
	@echo Fast C++03 FEATURE didn\'t work.
	@echo ==============================================================================
	@echo Building the standard version of FEATURE
	@echo ==============================================================================
	$(MAKE) clean
	cd src && $(MAKE) standard
	@echo Testing standard FEATURE
	cd tests && $(MAKE)
	@echo ==============================================================================
	@echo SUCCESS! Standard FEATURE has been compiled and tested.
	@echo ==============================================================================
