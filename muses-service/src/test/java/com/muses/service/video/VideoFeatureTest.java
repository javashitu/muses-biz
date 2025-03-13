package com.muses.service.video;

/**
 * @ClassName VideoFeatureTest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2025/3/2 11:05
 */
public class VideoFeatureTest {
    public static void main(String[] args) {
        String metaFeature = "0.0;0.0;0.0;0.0;0.37117866;0.26135638;0.13471963;0.0;0.052314226;0.0;0.30208546;0.0;0.0;0.0;0.36339176;0.0;0.11152799;0.513169;0.37555167;0.0;0.0;0.0488257;0.0;0.2597166;0.20211612;0.25877306;0.0;0.044799305;0.0;0.38051382;0.0;0.49333963";
        StringBuilder stringBuilder = new StringBuilder("0");
        for(int i = 0;i < 31;i++){
            stringBuilder.append(";0");
        }
        System.out.println("the arr length is " + stringBuilder.toString().split(";").length);
        System.out.println(stringBuilder);
    }
}
