# -*- coding: utf-8 -*-
import os, shutil, hashlib, filecmp
from MyUtils import read_file, java_files_from_dir, write_file, copytree, rm_dup

github_original_path = '/Users/Falcon/Desktop/GitArchive/Git_20161108/'
github_with_testcode_path = '/Users/Falcon/Desktop/GitArchive/Git_with_test/'
github_with_testcode_over_avg_path = '/Users/Falcon/Desktop/GitArchive/Git_with_test_over_avg/'
github_with_testcode_under_avg_path = '/Users/Falcon/Desktop/GitArchive/Git_with_test_under_avg/'
github_without_testcode_path = '/Users/Falcon/Desktop/GitArchive/Git_without_test/'
github_without_testcode_over_avg_path = '/Users/Falcon/Desktop/GitArchive/Git_without_test_over_avg/'
github_without_testcode_under_avg_path = '/Users/Falcon/Desktop/GitArchive/Git_without_test_under_avg/'

def rm_dup_java(src):
    javafiles = java_files_from_dir(src)
    final_list = list()
    flag = 0
    for file in javafiles:
        print file
        if not file in final_list:
            for f in final_list:
                if filecmp.cmp(file, f) == 1:
                    os.remove(file)
                    print 'Deleting ', file
                    flag = 1
                    break

            if flag == 0:
                final_list.append(file)
            elif flag == 1:
                flag = 0

def convertingToJava(src):
    print 'Conver'
    for dirpath, dirnames, files in os.walk(src):
        for file in files:
            if file.endswith('.txt'):
                os.rename(dirpath + '/' + file, dirpath + '/' + file.split('.')[0] + '.java')

def deleteEmptyDirs(src):
    for dirpath, dirnames, files in os.walk(src):
        if not dirnames and not files:
            shutil.rmtree(dirpath)

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

def count_projects(dir):
    count_project = 0
    for i in os.listdir(dir):
        if i == '.DS_Store':
            continue
        count_project += 1
    print "Projects count: ", count_project

def count_javafiles(dir):
    count_javafile = 0
    javafiles = java_files_from_dir(dir)
    for i in javafiles:
        if i == '.DS_Store':
            continue
        count_javafile += 1
    print "Javafiles count: ", count_javafile

def copy_projects_from_gitbase(listfile, gitbase, dst):
    print 'Copying the target projects...'
    count_copy = 0
    target_list = read_file(listfile)
    for i in target_list.split('\n'):
        if i == '.DS_Store':
            continue
        count_copy += 1
        print "Processing " + i + '...' + str(count_copy)
        try:
            copytree(gitbase + i, dst + i)
        except:
            print '!!!!!!!!!!!!!!!!!!!!!!!! Copy failed with ' + i
    print 'Done..'

def copyConfirm(listfile, dir):
    content = read_file(listfile)
    projects = content.split('\n')

    error_count = 0
    for i in os.listdir(dir):
        if i in projects:
            continue
        else:
            error_count += 1
            print i
    print "error count: ", error_count


def calcAvgJavafiles(dir):
    project_list = os.listdir(dir)
    total_project = len(project_list)
    total_javafiles = 0
    for project in project_list:
        javafiles = java_files_from_dir(dir + project)
        for file in javafiles:
            total_javafiles += 1
    print total_project
    print total_javafiles
    return float(total_javafiles / total_project)


def splitProjectsByAvg(base, avg_count, over_dir, under_dir):
    project_list = os.listdir(base)

    for project in project_list:
        basic_str = "Project " + str(project) + " goes to "
        javafile_count = 0
        javafiles = java_files_from_dir(base + project)
        for file in javafiles:
            javafile_count += 1

        if javafile_count > avg_count:
            print basic_str + "OVER (%s)" % javafile_count
            try: copytree(base + project, over_dir + project)
            except: print "Copy failed with " + project
        else:
            print basic_str + "UNDER (%s)" % javafile_count
            try: copytree(base + project, under_dir + project)
            except: print "Copy failed with " + project

if __name__ == '__main__':
    # #Count original number of projects
    # count_projects(github_original_path)
    #
    # #Count original number of javafiles
    # count_javafiles(github_original_path)
    #
    # # Count projects with test
    # count_projects(github_with_testcode_path)
    #
    # # Count javafiles with test
    # count_javafiles(github_with_testcode_path)
    #
    # # Count projects without test
    # count_projects(github_without_testcode_path)
    #
    # # Count javafiles without test
    # count_javafiles(github_without_testcode_path)
    #
    # # Count projects without test and over avg
    # count_projects(github_without_testcode_over_avg_path)
    #
    # # Count javafiles without test and over avg
    # count_javafiles(github_without_testcode_over_avg_path)

    # Copy paste projects in listfile from gitbase
    # listfile = '/Users/Falcon/Desktop/projects_without_testcode.txt'
    # copy_projects_from_gitbase(listfile, github_original_path, github_without_testcode_path)

    # Confirm  // 복사할때 자꾸 이상한 프로젝트가 함께 실려간다. ㅡㅡ^
    # listfile = '/Users/Falcon/Desktop/projects_without_testcode.txt'
    # copyConfirm(listfile, github_without_testcode_path)

    # # Split the projects with test
    # avg_javafile_count = calcAvgJavafiles(github_with_testcode_path)
    # print "--- The average = " + str(avg_javafile_count)+ " ---"
    # splitProjectsByAvg(github_with_testcode_path, avg_javafile_count, github_with_testcode_over_avg_path, github_with_testcode_under_avg_path)

    # # Split the projects without test
    # avg_javafile_count = calcAvgJavafiles(github_without_testcode_path)
    # print "--- The average = " + str(avg_javafile_count) + " ---"
    # splitProjectsByAvg(github_without_testcode_path, avg_javafile_count, github_without_testcode_over_avg_path, github_without_testcode_under_avg_path)


    # test = '/Users/Falcon/Desktop/Test_code_augmentation/aa/'
    # src = '/Users/Falcon/Desktop/Test_code_augmentation/facoy_new/'
    # refineDirectories(src)  # removing useless dirs
    # convertingToJava(src)   # convert every txt files to java files
    # rm_dup(src)             # duplicate
    # rm_dup_java(src)        # duplicate_2
    # deleteEmptyDirs(src)    # removing empty dirs


    pass