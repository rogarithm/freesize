package org.rogarithm.presize.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ImgUploadServiceTest {
    @Test
    public void test_parsing_DataBufferLimitException_err_msg() {
        String errorMsg = "Exceeded limit on max bytes to buffer : 2097152";
        String problematicSize = errorMsg.split(":")[1].replaceAll(" ", "");
        Assertions.assertThat(Integer.parseInt(problematicSize)).isEqualTo(2097152);
    }
}
