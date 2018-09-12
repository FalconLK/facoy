#-*- coding: utf-8 -*-
import os

target_path = '/Users/Falcon/Desktop/IJA/dataset/Experiment/targets_Done/'
log_path = '/Users/Falcon/Desktop/Tracing/Score_logs/'

#Total : 14219

if __name__ == '__main__':
    #Getting all files from the dir.
    target_files = [f for f in os.listdir(target_path) if os.path.isfile(os.path.join(target_path, f))]
    log_files = [f for f in os.listdir(log_path) if os.path.isfile(os.path.join(log_path, f))]

    #['DS_Store'] 제거해야함..
    target_files = target_files[1:]
    target_files.sort(key=lambda f: int(filter(str.isdigit, f)))

    for i in range(len(target_files)):
        target_name = target_files[i].split('.')[0]
        log_file_old = log_path + log_files[i]
        log_file_new = log_path + target_name + '.txt'
        print log_file_old, " /// ", log_file_new
        os.rename(log_file_old, log_file_new)