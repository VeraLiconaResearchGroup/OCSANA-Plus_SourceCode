#!/usr/bin/env python

# Parser to translate DrugBank XML into drug-gene interaction data
#
# Copyright Vera-Licona Research Group (C) 2015
#
# This software is licensed under the Artistic License 2.0, see the
# LICENSE file or
# http://www.opensource.org/licenses/artistic-license-2.0.php for
# details

import argparse
from lxml import etree
from collections import defaultdict
import simplejson as json
import logging

# Helper class to handle JSON encoding with sets
class SetEncoder(json.JSONEncoder):
    def default(self, obj):
       if isinstance(obj, set) or isinstance(obj, frozenset):
          return sorted(list(obj))
       return json.JSONEncoder.default(self, obj)

def parse_xml(filename):
    # Return a dict with drug data from the DrugBank XML file
    #
    # The result is keyed with DrugBank IDs and contains
    logging.info(u"Reading XML file")
    try:
        tree = etree.parse(filename)
    except IOError:
        raise ValueError("Could not read DB file {0}".format(filename))

    logging.info(u"Parsing XML tree")
    root = tree.getroot()

    ns = {'dbns': root.nsmap[None]}

    drugs = {}

    for drug in root.findall('dbns:drug', namespaces=ns):
        drug_name = drug.findtext('dbns:name', namespaces=ns).strip()
        logging.debug(u"Drug: {}".format(drug_name))

        dbid = drug.findtext('dbns:drugbank-id[@primary="true"]', namespaces=ns).strip()
        all_dbids = set(entry.text.strip() for entry in drug.findall('dbns:drugbank-id', namespaces=ns))
        drug_groups = set(group.text.strip().upper() for group in drug.findall('dbns:groups/dbns:group', namespaces=ns))

        drugs[dbid] = {"name": drug_name, "dbids": all_dbids, "groups": drug_groups, "targets": []}

        for target in drug.find('dbns:targets', namespaces=ns).findall('dbns:target', namespaces=ns):
            target_name = target.findtext('dbns:name', namespaces=ns).strip()
            logging.debug(u"Target: {}".format(target_name))
            polypeptides = target.findall("dbns:polypeptide", namespaces=ns)

            for polypeptide in polypeptides:
                uniprot_id =  polypeptide.findtext('dbns:external-identifiers/dbns:external-identifier[dbns:resource="UniProtKB"]/dbns:identifier', namespaces=ns)
                if uniprot_id:
                    uniprot_id = uniprot_id.strip()
                else:
                    uniprot_id = polypeptide.get("id").strip()

                logging.debug(u"Polypeptide ID: {}".format(uniprot_id))

                actions = target.findall('dbns:actions/dbns:action', namespaces=ns)
                for action in actions:
                    action_name = action.text.strip().upper()
                    logging.debug(u"Action: {}".format(action_name))
                    action_desc = {"target": uniprot_id, "drug": dbid, "action": action_name}
                    drugs[dbid]["targets"].append(action_desc)

    logging.info(u"Found {} drugs in DrugBank XML file".format(len(drugs)))
    return drugs

def main():
    # Set up argument processing
    parser = argparse.ArgumentParser(description="DrugBank data parser")

    parser.add_argument("drugbank_db_file", help="XML file of DrugBank data (download from www.drugbank.ca)")
    parser.add_argument("json_output_file", help="JSON file to write with interaction data")
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
    drugs = parse_xml(args.drugbank_db_file)

    # Write the result
    logging.info(u"Writing JSON file")
    with open(args.json_output_file, 'w') as outfile:
        json.dump(drugs, outfile, cls=SetEncoder)


if __name__ == "__main__":
    main()
