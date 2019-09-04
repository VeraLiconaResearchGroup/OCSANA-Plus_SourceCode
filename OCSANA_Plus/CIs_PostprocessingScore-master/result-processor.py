#!/usr/bin/env python

import argparse
import logging
import simplejson as json

# Build a map assigning each (singleton) PDB code to its UniProt ID
def proteome_map_from_file(proteome_filename):
    with open(proteome_filename) as proteome_file:
        proteome = json.load(proteome_file)

    proteome_map = {}
    for upid in proteome:
        for pdb in proteome[upid]['canonical'] + proteome[upid]['mutant']:
            proteome_map[pdb.upper()] = upid

    return proteome_map

# Build a map encoding the results in a results file, using proteome_map to find UniProt IDs
def results_from_file(results_filename, proteome_map):
    logging.info("Processing results file {}".format(results_filename))
    results = {}
    with open(results_filename) as result_file:
        for line in result_file:
            if len(line.split()) >= 5:
                pdb, ligand, chain, index, score = line.split()
                upid = proteome_map[pdb.upper()]
                if upid not in results:
                    results[upid] = []
                results[upid].append({"pdb": pdb, "ligand": ligand, "chain": chain, "index": int(index), "score": float(score)})

    return results

def main():
    # Set up argument processing
    parser = argparse.ArgumentParser(description="DrugFEATURE pipeline results processor")

    parser.add_argument("proteome_file", help="PDB proteome map file")
    parser.add_argument("result_files", nargs="+", metavar="file", help="DrugFEATURE results file to process")
    parser.add_argument('-1o', "--output_file", help="File to store results")
    parser.add_argument('-v', '--verbose', action="count", default=0, help="Print verbose logs (may be used multiple times)")

    args = parser.parse_args()

    # Set up logging
    if args.verbose == 0:
        log_level = logging.WARNING
    elif args.verbose == 1:
        log_level = logging.INFO
    else:
        log_level = logging.DEBUG

    logging.basicConfig(level = log_level)

    # Process inputs
    proteome_map = proteome_map_from_file(args.proteome_file)

    results = {}
    for result_file in args.result_files:
        results.update(results_from_file(result_file, proteome_map))

    logging.info("Processed results for {} proteins".format(len(results)))

    # Write out results
    output_filename = args.output_file
    logging.info("Writing results to {}".format(output_filename))
    with open(output_filename, 'w') as output_file:
        json.dump(results, output_file)

if __name__ == "__main__":
    main()
