/*
 * Copyright (c) 2015, 2015, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.graal.lir.ssa;

import java.util.List;

import jdk.vm.ci.code.TargetDescription;

import com.oracle.graal.compiler.common.cfg.AbstractBlockBase;
import com.oracle.graal.compiler.common.cfg.AbstractControlFlowGraph;
import com.oracle.graal.lir.LIR;
import com.oracle.graal.lir.LIRInsertionBuffer;
import com.oracle.graal.lir.LIRInstruction;
import com.oracle.graal.lir.gen.LIRGenerationResult;
import com.oracle.graal.lir.gen.LIRGeneratorTool;
import com.oracle.graal.lir.gen.PhiResolver;
import com.oracle.graal.lir.phases.PreAllocationOptimizationPhase;

public final class SSADestructionPhase extends PreAllocationOptimizationPhase {

    @Override
    protected <B extends AbstractBlockBase<B>> void run(TargetDescription target, LIRGenerationResult lirGenRes, List<B> codeEmittingOrder, List<B> linearScanOrder,
                    PreAllocationOptimizationContext context) {
        LIRGeneratorTool lirGen = context.lirGen;
        LIR lir = lirGenRes.getLIR();
        AbstractControlFlowGraph<?> cfg = lir.getControlFlowGraph();
        for (AbstractBlockBase<?> block : cfg.getBlocks()) {
            doBlock(block, lir, lirGen);
        }
    }

    private static void doBlock(AbstractBlockBase<?> block, LIR lir, LIRGeneratorTool lirGen) {
        if (block.getPredecessorCount() > 1) {
            for (AbstractBlockBase<?> pred : block.getPredecessors()) {

                List<LIRInstruction> instructions = lir.getLIRforBlock(pred);

                int insertBefore = SSAUtil.phiOutIndex(lir, pred);

                PhiResolver resolver = PhiResolver.create(lirGen, new LIRInsertionBuffer(), instructions, insertBefore);
                SSAUtil.forEachPhiValuePair(lir, block, pred, resolver::move);
                resolver.dispose();

                SSAUtil.removePhiOut(lir, pred);
            }
            SSAUtil.removePhiIn(lir, block);
        }
    }

}
