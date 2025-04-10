cmake_minimum_required(VERSION 3.10.2)

project("tess-two")

add_library(tess-two SHARED
        native-lib.cpp)

find_library(log-lib log)

target_link_libraries(tess-two ${log-lib})
