/*
 * Copyright 2016 Axibase Corporation or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * https://www.axibase.com/atsd/axibase-apache-2.0.pdf
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.axibase.tsd.plain;

import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractInsertCommandTest {

    @Test
    public void testNormalize() throws Exception {
        assertEquals("simple", AbstractInsertCommand.normalize("simple"));
        assertEquals("\"gaps gaps gaps\"", AbstractInsertCommand.normalize("gaps gaps gaps"));
    }

    @Test
    public void testClean() throws Exception {
        //Entity, metric and tag names must not contain the following characters: space, quote, double quote.
        assertEquals("str_name_of_tag", AbstractInsertCommand.clean("str name'of\"tag"));
        assertEquals("str_name_of_tag", AbstractInsertCommand.clean(" str\rname\tof\"tag "));
    }
}