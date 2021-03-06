/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.graal.truffle.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.oracle.graal.truffle.test.nodes.AbstractTestNode;
import com.oracle.graal.truffle.test.nodes.ReadOnlyArrayListConstantNode;
import com.oracle.graal.truffle.test.nodes.RootTestNode;
import com.oracle.truffle.api.frame.FrameDescriptor;

public class ReadOnlyArrayListPartialEvaluationTest extends PartialEvaluationTest {

    public static Object constant42() {
        return 42;
    }

    @Before
    public void before() {
        InstrumentationTestMode.set(true);
    }

    @Override
    @After
    public void after() {
        super.after();
        InstrumentationTestMode.set(false);
    }

    @Test
    public void constantValue() {
        FrameDescriptor fd = new FrameDescriptor();
        AbstractTestNode result = new ReadOnlyArrayListConstantNode(42);
        assertPartialEvalEquals("constant42", new RootTestNode(fd, "constantValue", result));
    }
}
