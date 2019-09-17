# gradml-hw1

a makefile is included for running it from the command line

the format for running the file is:
	java Tree <train-file> <validation-file> <test-file> <-options>
	
	options
		v|V runs variance impurity instead of default entropy
		e|E runs entropy, although set as default
		p|P after building the tree, prunes it
		d|D depth based validation
	can combine one (e,v) with one (p,d)
	