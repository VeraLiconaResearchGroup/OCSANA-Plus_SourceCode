#!/usr/bin/env python

# Parser to translate UniProt XML into protein metadata
#
# Copyright Vera-Licona Research Group (C) 2016
#
# This software is licensed under the Artistic License 2.0, see the
# LICENSE file or
# http://www.opensource.org/licenses/artistic-license-2.0.php for
# details

import argparse
from lxml import etree
import simplejson as json
import logging

# Helper class to handle JSON encoding with sets
class SetEncoder(json.JSONEncoder):
    def default(self, obj):
       if isinstance(obj, set) or isinstance(obj, frozenset):
          return sorted(list(obj))
       return json.JSONEncoder.default(self, obj)

def parse_xml(filename):
    # Return dict of metadata
    #
    # The result is keyed with UniProt IDs and contains name, HUGO
    # gene IDs, HUGO gene names, function descriptions, and
    # information about the drugs that target that protein.
    logging.info(u"Reading XML file")
    try:
        tree = etree.parse(filename)
    except IOError:
        raise ValueError("Could not read DB file {0}".format(filename))

    logging.info(u"Parsing XML tree")
    root = tree.getroot()

    ns = {'dbns': root.nsmap[None]}

    proteins = {}

    for protein in root.findall("dbns:entry", namespaces=ns):
        primary_id = protein.findtext("dbns:accession", namespaces=ns).strip()
        logging.debug("Processing protein {}".format(primary_id))

        all_ids = [accession.text.strip() for accession in protein.findall("dbns:accession", namespaces=ns)]
        protein_name_entry = protein.find("dbns:protein", namespaces=ns).find("dbns:recommendedName", namespaces=ns)

        if protein_name_entry is None:
            protein_name_entry = protein.find("dbns:protein", namespaces=ns)[0]
        protein_name = protein_name_entry.findtext("dbns:fullName", namespaces=ns).strip()

        gene_entry = protein.find("dbns:gene", namespaces=ns)
        if gene_entry is None:
            gene_names = []
        else:
            gene_names = [name.text.strip() for name in gene_entry.findall("dbns:name", namespaces=ns)]

        function_entry = protein.find("dbns:comment[@type='function']", namespaces=ns)
        if function_entry is None:
            function = ""
        else:
            function = function_entry.findtext("dbns:text", namespaces=ns).strip()

        isoforms = {}
        for refseq_entry in protein.findall("dbns:dbReference[@type='RefSeq']", namespaces=ns):
            refseq_id = refseq_entry.get("id").split(".")[0]
            try:
                isoform_id = refseq_entry.find("dbns:molecule", namespaces=ns).get("id").split("-")[1]
            except AttributeError:
                isoform_id = None

            if isoform_id not in isoforms:
                isoforms[isoform_id] = []
            isoforms[isoform_id].append(refseq_id)

        protein_data = {"upids": all_ids, "name": protein_name, "geneNames": gene_names, "function": function, "isoforms": isoforms}
        logging.debug("Protein data: {}".format(protein_data))
        proteins[primary_id] = protein_data

    logging.info(u"Found {} proteins in UniProt XML file".format(len(proteins)))
    return proteins

def main():
    # Set up argument processing
    parser = argparse.ArgumentParser(description="DrugBank data parser")

    parser.add_argument("drugbank_db_file", help="XML file of UniProt data (download from http://www.uniprot.org/uniprot/?query=proteome:UP000005640)")
    parser.add_argument("json_output_file", help="JSON file to write with protein data")
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

    # Parse the XML file into a dict for JSON serialization
    proteins = parse_xml(args.drugbank_db_file)

    # Write the result
    logging.info(u"Writing JSON file")
    with open(args.json_output_file, 'w') as outfile:
        json.dump(proteins, outfile, cls=SetEncoder)


if __name__ == "__main__":
    main()
