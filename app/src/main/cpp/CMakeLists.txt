cmake_minimum_required(VERSION 3.22.1)
project("usagi-native")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(usagi-native SHARED
    native_zip_writer.cpp
    native_image_probe.cpp
)

find_library(log-lib log)

target_link_libraries(usagi-native
    ${log-lib}
    z
)