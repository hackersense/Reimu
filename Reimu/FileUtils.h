#pragma once

#include <iostream>
#include <fstream>
#include <string>
#include <vector>

class FileUtils {
public:
    static bool save(const std::string& filepath, const std::string& content, bool append) {
        std::ofstream file;

        if (append) {
            file.open(filepath, std::ios::out | std::ios::app);
        } else {
            file.open(filepath, std::ios::out | std::ios::trunc);
        }

        if (!file.is_open()) {
            std::cerr << "Failed to open file: " << filepath << std::endl;
            return false;
        }

        file << content;
        file.close();

        return true;
    }

    static std::string read(const std::string& filepath) {
        std::ifstream file(filepath);
        std::string line;
        std::string content;

        if (!file.is_open()) {
            std::cerr << "Failed to open file: " << filepath << std::endl;
            return "";
        }

        while (std::getline(file, line)) {
            content += line + "\n";
        }
        file.close();

        return content;
    }

    static std::vector<std::string> readList(const std::string& filepath) {
        std::ifstream file(filepath);
        std::string line;
        std::vector<std::string> content;

        if (!file.is_open()) {
            std::cerr << "Failed to open file: " << filepath << std::endl;
            return content;
        }

        while (std::getline(file, line)) {
            content.push_back(line);
        }
        file.close();

        return content;
    }

    static bool exists(const std::string& filePath) {
        std::ifstream file(filePath);
        return file.is_open();
    }
};