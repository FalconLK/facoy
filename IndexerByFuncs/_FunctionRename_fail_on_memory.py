# -*- coding: utf-8 -*-

# from GitSearch.MyUtils import write_file, read_file
# import os
# if __name__ == '__main__':
    # count = 0
    # for filename in os.listdir("/Users/Falcon/Desktop/IJA/IJA_functions"):
    #     count += 1
    #     print count
    #     if filename.endswith('txt'):
    #         new = filename.split('.')[0]
    #         new = '/Users/Falcon/Desktop/IJA/IJA_functions/' + new + '.java'
    #
    #         file = '/Users/Falcon/Desktop/IJA/IJA_functions/' + file
    #         os.rename(file, new)


    # for file in os.listdir('/Users/Falcon/Desktop/IJA/IJA_functions'):
    #     pure_name = file.split('.')[0]
    #     if int(pure_name) >



    # -*- coding: utf-8 -*-
    # @author: Peter Lamut

import argparse
import os
import shutil

N = 1000  # the number of files in seach subfolder folder


def move_files(abs_dirname):
    """Move files into subdirectories."""

    files = [os.path.join(abs_dirname, f) for f in os.listdir(abs_dirname)]

    i = 0
    curr_subdir = None

    for f in files:
        # create new subdir if necessary
        if i % N == 0:
            subdir_name = os.path.join(abs_dirname, '{0:03d}'.format(i / N + 1))
            os.mkdir(subdir_name)
            curr_subdir = subdir_name

        # move file to current dir
        f_base = os.path.basename(f)
        shutil.move(f, os.path.join(subdir_name, f_base))
        i += 1

def main(src_dir):
    """Module's main entry point (zopectl.command)."""

    if not os.path.exists(src_dir):
        raise Exception('Directory does not exist ({0}).'.format(src_dir))

    move_files(os.path.abspath(src_dir))

def java_files_from_dir(source_path):
    javafiles = []
    for path, subdirs, files in os.walk(source_path):
        for name in files:
            if name.endswith('.java'):
                javafiles.append(os.path.join(path, name))
    return javafiles

if __name__ == '__main__':
    # main('/Users/Falcon/Desktop/IJA/IJA_functions')
    # main('/Users/Falcon/Desktop/test/test')

    javafiles = java_files_from_dir('/Users/Falcon/Desktop/test/test_result/')
    count = 0
    for i in javafiles:
        count += 1

    print count
