package com.example.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.exam.entity.Question;
import com.example.exam.mapper.QuestionMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService extends ServiceImpl<QuestionMapper, Question> {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionMapper questionMapper;

    public boolean importQuestions(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.error("导入文件为空");
            return false;
        }

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                logger.error("Excel 文件中没有工作表");
                return false;
            }

            List<Question> questions = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // 验证行数据是否完整
                if (row.getLastCellNum() < 6) {
                    logger.error("第 {} 行数据不完整", i + 1);
                    return false;
                }

                Question question = new Question();
                question.setContent(row.getCell(0).getStringCellValue());
                question.setOptionA(row.getCell(1).getStringCellValue());
                question.setOptionB(row.getCell(2).getStringCellValue());
                question.setOptionC(row.getCell(3).getStringCellValue());
                question.setOptionD(row.getCell(4).getStringCellValue());
                question.setCorrectAnswer(row.getCell(5).getStringCellValue());

                questions.add(question);
            }

            // 使用 saveBatch 方法进行批量插入
            return this.saveBatch(questions);

        } catch (Exception e) {
            logger.error("导入题目失败", e);
            return false;
        }
    }

    public List<Question> getRandomQuestions(int count) {
        if (count <= 0) {
            logger.error("获取随机题目的数量必须大于 0");
            return new ArrayList<>();
        }

        return questionMapper.selectPage(new Page<>(1, count), null).getRecords();
    }
}