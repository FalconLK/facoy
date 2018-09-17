# -*- coding: utf-8 -*-
import os, shutil, hashlib, time
from MyUtils import read_file, java_files_from_dir, write_file, copytree, rm_dup

def refineDirectories(src):
    progress = 0
    for dirpath, dirnames, files in os.walk(src):
        progress = progress + 1
        if progress % 1000 == 0: print 'Parsing: ', progress
        if dirpath.split('/')[-1] == 'CodeFragments' or dirpath.split('/')[-1] == 'SynthesizedMethods':
            print "Directory " + dirpath + "is being deleted.."
            shutil.rmtree(dirpath)

        if dirpath.split('/')[-1] == 'CompleteMethod':
            for file in files:
                new_dir = '/'.join(dirpath.split('/')[:-1]) + '/' + file.split('.')[0]
                try:os.mkdir(new_dir)
                except:pass
                shutil.copyfile(dirpath + '/' + file, new_dir + '/' + file)
            shutil.rmtree(dirpath)

def filtering_only_test_code(src, dst):
    progress = 0
    test_code_count = 0
    if not os.path.isdir(dst):
        os.mkdir(dst)

    project_only_test = set()
    for javafile in java_files_from_dir(src):
        progress += 1
        if progress % 1000 == 0: print 'Parsing: ', progress
        if ('test' or 'tests') in javafile.split('/'):  # and 'test' in javafile.split('/')[-1]:
            test_code_count += 1
            project_only_test.add('/'.join(javafile.split('/')[:7]))

    progress = 0
    for i in project_only_test:
        progress = progress + 1
        if progress % 1000 == 0: print 'Copy: ', progress
        target_destination_detail = dst + i.split('/')[6]

        if not os.path.isdir(target_destination_detail):
            os.mkdir(target_destination_detail)
            copytree(i, target_destination_detail)
        else:
            print 'The directory is already existing..'

if __name__ == '__main__':
    src = '/Users/Falcon/Desktop/GitArchive/Git_testcode_only/'
    # refineDirectories(src)
    # check_for_duplicates(src)

    # rm_dup(src)

    # javafiles = java_files_from_dir(src)
    # count = 0
    # for i in javafiles:
    #     count += 1
    #
    # print count


    aset = set()
    contents = read_file('/Users/Falcon/Desktop/projects_with_test_code_only.txt')
    for i in contents.split('\n'):
        aset.add(i)

    for i in aset:
        write_file('/Users/Falcon/Desktop/projects_test_code_only.txt', i)

    pass