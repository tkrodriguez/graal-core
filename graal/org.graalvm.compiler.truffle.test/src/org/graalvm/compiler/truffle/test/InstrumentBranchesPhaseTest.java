/*
 * Copyright (c) 2016, 2016, Oracle and/or its affiliates. All rights reserved.
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
package org.graalvm.compiler.truffle.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.graalvm.compiler.truffle.OptimizedCallTarget;
import org.graalvm.compiler.truffle.TruffleCompilerOptions;
import org.graalvm.compiler.truffle.phases.InstrumentBranchesPhase;
import org.graalvm.compiler.truffle.test.nodes.AbstractTestNode;
import org.graalvm.compiler.truffle.test.nodes.RootTestNode;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class InstrumentBranchesPhaseTest extends PartialEvaluationTest {

    public static class SimpleIfTestNode extends AbstractTestNode {
        private int constant;

        public SimpleIfTestNode(int constant) {
            this.constant = constant;
        }

        @Override
        public int execute(VirtualFrame frame) {
            if (constant < 0) {
                return -1 * constant;
            } else {
                return 1;
            }
        }
    }

    public static class TwoIfsTestNode extends AbstractTestNode {
        private int x;
        private int y;

        public TwoIfsTestNode(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int execute(VirtualFrame frame) {
            if (x < 0) {
                return -1 * x;
            } else {
                if (y < 0) {
                    return -1 * y;
                } else {
                    return x * y;
                }
            }
        }
    }

    @Override
    protected void beforeInitialization() {
        Assert.assertFalse(TruffleCompilerOptions.TruffleInstrumentBranches.getValue());
        TruffleCompilerOptions.TruffleInstrumentBranches.setValue(true);
    }

    @After
    public void disableInstrumentAfterTests() {
        TruffleCompilerOptions.TruffleInstrumentBranches.setValue(false);
    }

    @Test
    public void simpleIfTest() {
        FrameDescriptor descriptor = new FrameDescriptor();
        SimpleIfTestNode result = new SimpleIfTestNode(5);
        RootTestNode rootNode = new RootTestNode(descriptor, "simpleIfRoot", result);
        boolean instrumentFlag = TruffleCompilerOptions.TruffleInstrumentBranches.getValue();
        boolean prettyFlag = TruffleCompilerOptions.TruffleInstrumentBranchesPretty.getValue();
        String filterFlag = TruffleCompilerOptions.TruffleInstrumentBranchesFilter.getValue();
        try {
            TruffleCompilerOptions.TruffleInstrumentBranches.setValue(true);
            TruffleCompilerOptions.TruffleInstrumentBranchesPretty.setValue(false);
            TruffleCompilerOptions.TruffleInstrumentBranchesFilter.setValue("*.*.execute");
            OptimizedCallTarget target = compileHelper("simpleIfRoot", rootNode, new Object[0]);
            Assert.assertTrue(target.isValid());
            target.call();
        } finally {
            TruffleCompilerOptions.TruffleInstrumentBranches.setValue(instrumentFlag);
            TruffleCompilerOptions.TruffleInstrumentBranchesPretty.setValue(prettyFlag);
            TruffleCompilerOptions.TruffleInstrumentBranchesFilter.setValue(filterFlag);
        }
        String stackOutput = InstrumentBranchesPhase.instrumentation.accessTableToList().get(0);
        Assert.assertTrue(stackOutput.contains("org.graalvm.compiler.truffle.test.InstrumentBranchesPhaseTest$SimpleIfTestNode.execute(InstrumentBranchesPhaseTest.java"));
        Assert.assertTrue(stackOutput.contains("[bci: 4]\n[0] state = ELSE(if=0#, else=1#)"));
        String histogramOutput = InstrumentBranchesPhase.instrumentation.accessTableToHistogram().get(0);
        Assert.assertEquals("  0: ********************************************************************************", histogramOutput);
    }

    @Test
    public void twoIfsTest() {
        FrameDescriptor descriptor = new FrameDescriptor();
        TwoIfsTestNode result = new TwoIfsTestNode(5, -1);
        RootTestNode rootNode = new RootTestNode(descriptor, "twoIfsRoot", result);
        boolean instrumentFlag = TruffleCompilerOptions.TruffleInstrumentBranches.getValue();
        boolean prettyFlag = TruffleCompilerOptions.TruffleInstrumentBranchesPretty.getValue();
        String filterFlag = TruffleCompilerOptions.TruffleInstrumentBranchesFilter.getValue();
        try {
            TruffleCompilerOptions.TruffleInstrumentBranches.setValue(true);
            TruffleCompilerOptions.TruffleInstrumentBranchesPretty.setValue(false);
            TruffleCompilerOptions.TruffleInstrumentBranchesFilter.setValue("*.*.execute");
            OptimizedCallTarget target = compileHelper("twoIfsRoot", rootNode, new Object[0]);
            Assert.assertTrue(target.isValid());
            // We run this twice to make sure that it comes first in the sorted access list.
            target.call();
            target.call();
        } finally {
            TruffleCompilerOptions.TruffleInstrumentBranches.setValue(instrumentFlag);
            TruffleCompilerOptions.TruffleInstrumentBranchesPretty.setValue(prettyFlag);
            TruffleCompilerOptions.TruffleInstrumentBranchesFilter.setValue(filterFlag);
        }
        String stackOutput1 = InstrumentBranchesPhase.instrumentation.accessTableToList().get(0);
        Assert.assertTrue(stackOutput1.contains("org.graalvm.compiler.truffle.test.InstrumentBranchesPhaseTest$TwoIfsTestNode.execute(InstrumentBranchesPhaseTest.java"));
        Assert.assertTrue(stackOutput1.contains("[bci: 4]\n[1] state = ELSE(if=0#, else=2#)"));
        String stackOutput2 = InstrumentBranchesPhase.instrumentation.accessTableToList().get(1);
        Assert.assertTrue(stackOutput2.contains("org.graalvm.compiler.truffle.test.InstrumentBranchesPhaseTest$TwoIfsTestNode.execute(InstrumentBranchesPhaseTest.java"));
        Assert.assertTrue(stackOutput2.contains("[bci: 18]\n[2] state = IF(if=2#, else=0#)"));
    }
}
