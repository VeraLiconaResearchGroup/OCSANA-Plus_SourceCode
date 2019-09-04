#!/usr/bin/env python
# coding: utf-8

import argparse
import logging
import os
import shutil
import subprocess
import tempfile

"""
Construct a link from source_file in the directory dest_dir.

If possible, this will be a hard link; if not, a symlink will be used.
"""
def link_into(source_file, dest_dir):
    target = os.path.join(dest_dir, os.path.basename(source_file))
    try:
        os.link(source_file, target)
    except OSError:
        os.symlink(source_file, target)

"""
Build an environment dictionary for subprocess which has been modified for DrugFEATURE
"""
def build_env(feature_dir):
    env = os.environ.copy()

    feature_bin_dir = os.path.join(feature_dir, "bin", "")
    env["PATH"] = feature_bin_dir + ":" + env["PATH"]

    feature_data_dir = os.path.join(feature_dir, "data")
    env["FEATURE_DIR"] = feature_data_dir

    feature_dssp_dir = os.path.join(feature_data_dir, "dssp")
    env["DSSP_DIR"] = feature_dssp_dir

    feature_pdb_dir = os.path.join(feature_data_dir, "pdb")
    env["PDB_DIR"] = feature_pdb_dir

    return env

"""
Set up the working directory for a DrugFEATURE run
"""
def setup_working_dir(working_dir, pdb_filename, df_dir):
    #TODO: copy DSSP file as well
    if not os.path.isdir(working_dir):
        raise ValueError("Could not find working directory {}".format(working_dir))

    if not os.path.isfile(pdb_filename):
        raise ValueError("Could not find PDB file {}".format(pdb_filename))

    link_into(pdb_filename, working_dir)

    dssp_filename = os.path.join(os.path.splitext(pdb_filename)[0] + ".dssp")
    if not os.path.isfile(dssp_filename):
        logging.info("No DSSP file found at {0}. Continuing.".format(dssp_filename))
    else:
        link_into(dssp_filename, working_dir)

def generate_cavity_points(df_dir, working_dir, env, pdb_code, ligand_name, ligand_chain, ligand_index):
    logging.info("Running step 1")

    # Write config file
    config_line = "\t".join(map(str, [pdb_code, ligand_name, ligand_chain, ligand_index])) + "\n"
    config_filename = os.path.join(working_dir, "1.cavity.point.config")
    with open(config_filename, 'w') as config_file:
        config_file.write(config_line)

    # Run tool
    tool = os.path.join(df_dir, "GenerateCavityPoint_Vectorize.pl")
    tool_call = [tool, config_filename, "6A"]

    log_filename = os.path.join(working_dir, "1.cavity.point.log")
    with open(log_filename, 'w') as log_file:
        result = subprocess.check_output(tool_call, env=env, cwd=working_dir, stderr=log_file)

    result_pdb_code, result_ligand_name, result_ligand_chain, result_ligand_index, result_num_nonhydro, result_num_points = result.split()

    orig_tuple = (pdb_code, ligand_name, ligand_chain, ligand_index)
    result_tuple = (result_pdb_code, result_ligand_name, result_ligand_chain, result_ligand_index)
    if orig_tuple != result_tuple:
        raise ValueError("Return from cavity point generator does not make sense: {} ≠ {}".format(orig_tuple, result_tuple))

    logging.info("Step 1 complete: found {} non-hydrogen atoms and {} points in cavity".format(result_num_nonhydro, result_num_points))
    return result_num_nonhydro, result_num_points

def compare_set_sites(df_dir, working_dir, env, pdb_code, ligand_name, ligand_chain, ligand_index):
    logging.info("Running step 2")

    # Write config file
    config_line = "{}_{}".format(pdb_code, ligand_name) + "\n"
    config_filename = os.path.join(working_dir, "2.compare.sites.config")
    with open(config_filename, 'w') as config_file:
        config_file.write(config_line)

    # Run tool
    tool = os.path.join(df_dir, "CompareTwoSetsSites.pl")
    working_dir = os.path.join(working_dir, "")
    df_parameter_dir = os.path.join(df_dir, "ParameterFiles")
    df_db_list = os.path.join(df_parameter_dir, "DrugSiteList.txt")
    df_db_vectors_dir = os.path.join(df_parameter_dir, "DrugBindingVectors", "") # The final "" ensures a trailing slash, needed due to DrugFEATURE quirk
    df_cavity_list = os.path.join(df_parameter_dir, "All1160Cavity.std")
    df_normalize_list = os.path.join(df_parameter_dir, "TcCutoff4Normalize.txt")
    output_filename = os.path.join(working_dir, "2.compare.sites.output")

    log_filename = os.path.join(working_dir, "2.compare.sites.log")
    with open(log_filename, 'w') as log_file:
        subprocess.check_call([tool, config_filename, working_dir, df_db_list, df_db_vectors_dir, df_cavity_list, df_normalize_list, output_filename], env=env, cwd=working_dir, stdout=log_file, stderr=log_file)

    logging.info("Step 2 complete")
    return os.path.abspath(output_filename)

def count_hits(df_dir, working_dir, env, set_sites_result_filename, pdb_code, ligand_name, ligand_chain, ligand_index, num_hydro, num_points):
    logging.info("Running step 3")

    # Write config file
    config_line = "\t".join([pdb_code, ligand_name, ligand_chain, ligand_index, num_hydro, num_points]) + "\n"
    config_filename = os.path.join(working_dir, "3.count.hits.config")
    with open(config_filename, 'w') as config_file:
        config_file.write(config_line)

    # Run tool
    tool = os.path.join(df_dir, "CountHit.pl")
    log_filename = os.path.join(working_dir, "3.count.hits.log")
    with open(log_filename, 'w') as log_file:
        result = subprocess.check_output([tool, config_filename, set_sites_result_filename, "1.9"], env=env, cwd=working_dir, stderr=log_file)

    result_pdb_code, result_ligand_name, result_ligand_chain, result_ligand_index, result_num_nonhydro, result_num_points, unknown1, unknown2, score = result.split()

    orig_tuple = (pdb_code, ligand_name, ligand_chain, ligand_index, num_hydro, num_points)
    result_tuple = (result_pdb_code, result_ligand_name, result_ligand_chain, result_ligand_index, result_num_nonhydro, result_num_points)
    if orig_tuple != result_tuple:
        raise ValueError("Return from cavity point generator does not make sense: {} ≠ {}".format(orig_tuple, result_tuple))

    logging.info("Step 3 complete: drugability score {}".format(score))
    return float(score)

def run_pipeline(df_dir, feature_dir, working_dir, pdb_code, ligand_name, ligand_chain, ligand_index):
    env = build_env(feature_dir)

    num_hydro, num_points = generate_cavity_points(df_dir, working_dir, env, pdb_code, ligand_name, ligand_chain, ligand_index)
    set_sites_result_filename = compare_set_sites(df_dir, working_dir, env, pdb_code, ligand_name, ligand_chain, ligand_index)
    score = count_hits(df_dir, working_dir, env, set_sites_result_filename, pdb_code, ligand_name, ligand_chain, ligand_index, num_hydro, num_points)
    return score

def main():
    # Set up argument processing
    parser = argparse.ArgumentParser(description="DrugFEATURE pipeline runner")

    parser.add_argument("pdb_code", help = "PDB ID to process (e.g. 1rv1)")
    parser.add_argument("ligand_name", help = "Ligand ID code (e.g. IMZ)")
    parser.add_argument("ligand_chain", help = "Chain containing ligand (e.g. A)")
    parser.add_argument("ligand_index", help = "Index of ligand on chain (e.g. 110)")
    parser.add_argument("-d", "--dest_dir", help = "Directory to store results (will create a unique subdirectory based on input) [default: auto-generate in /tmp]", default=None)
    parser.add_argument("-p", "--pdb_dir", help = "Directory containing divided PDB heirarchy")
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

    # Process arguments
    pdb_code = args.pdb_code.lower()
    ligand_name = args.ligand_name
    ligand_chain = args.ligand_chain
    ligand_index = args.ligand_index
    dest_dir = args.dest_dir
    pdb_dir = args.pdb_dir

    pdb_subdir = pdb_code[1:3]

    pdb_filename = os.path.join(os.path.abspath(pdb_dir), pdb_subdir, pdb_code + ".pdb")

    logging.info("Processing PDB code {} (ligand name {}, chain {}, index {}) from PDB directory {}".format(pdb_code, ligand_name, ligand_chain, ligand_index, pdb_dir))

    # Set up temporary working directory and environment
    scriptdir = os.path.abspath(os.path.dirname(__file__))
    feature_dir = os.path.join(scriptdir, "feature")
    df_dir = os.path.join(scriptdir, "DrugFEATURE")

    if dest_dir is None:
        dest_dir = os.path.join(tempfile.mkdtemp(), "")
        logging.info("Using temporary directory {}".format(dest_dir))
    else:
        unique_dir_suffix = os.path.join(pdb_subdir, pdb_code, ligand_name, ligand_chain, ligand_index)
        dest_dir = os.path.join(os.path.abspath(dest_dir), unique_dir_suffix, "")
        try:
            logging.info("Using user-specified output directory {}".format(dest_dir))
            os.makedirs(dest_dir)
        except OSError:
            if not os.path.isdir(dest_dir):
                raise

    setup_working_dir(dest_dir, pdb_filename, df_dir)

    # Run DrugFEATURE pipeline
    score = run_pipeline(df_dir, feature_dir, dest_dir, pdb_code, ligand_name, ligand_chain, ligand_index)

    result_line = "\t".join(map(str, [pdb_code, ligand_name, ligand_chain, ligand_index, score])) + "\n"
    result_filename = os.path.join(dest_dir, "result.txt")
    with open(result_filename, 'w') as result_file:
        result_file.write(result_line)

if __name__ == "__main__":
    main()
