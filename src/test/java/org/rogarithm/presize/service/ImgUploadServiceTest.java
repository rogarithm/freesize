package org.rogarithm.presize.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ImgUploadServiceTest {
    @Test
    public void test_parsing_DataBufferLimitException_err_msg() {
        String errorMsg = "Exceeded limit on max bytes to buffer : 2097152";
        assertThat(errorMsg.split(":").length).isEqualTo(2);
        String problematicSize = errorMsg.split(":")[1].replaceAll(" ", "");
        assertThat(Integer.parseInt(problematicSize)).isEqualTo(2097152);
    }

    @Test
    public void test_fail_parsing_DataBufferLimitException_err_msg() {
        String errorMsg = "There is no colon in err msg!";
        assertThat(errorMsg.split(":").length).isEqualTo(1);
    }
}
