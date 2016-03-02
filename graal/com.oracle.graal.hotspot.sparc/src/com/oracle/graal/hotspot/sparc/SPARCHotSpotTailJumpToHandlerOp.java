/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.graal.hotspot.sparc;

import jdk.vm.ci.code.Register;

import com.oracle.graal.asm.sparc.SPARCMacroAssembler;
import com.oracle.graal.asm.sparc.SPARCMacroAssembler.ScratchRegister;
import com.oracle.graal.compiler.common.spi.ForeignCallLinkage;
import com.oracle.graal.lir.LIRInstructionClass;
import com.oracle.graal.lir.Opcode;
import com.oracle.graal.lir.asm.CompilationResultBuilder;
import com.oracle.graal.lir.sparc.SPARCCall;

/**
 * Removes the current frame and tail calls the uncommon trap routine.
 */
@Opcode("TAIL_JUMP_TO_HANDLER")
final class SPARCHotSpotTailJumpToHandlerOp extends SPARCHotSpotEpilogueOp {
    public static final LIRInstructionClass<SPARCHotSpotTailJumpToHandlerOp> TYPE = LIRInstructionClass.create(SPARCHotSpotTailJumpToHandlerOp.class);
    public static final SizeEstimate SIZE = SizeEstimate.create(32);

    private final ForeignCallLinkage handler;

    protected SPARCHotSpotTailJumpToHandlerOp(ForeignCallLinkage handler) {
        super(TYPE, SIZE);
        this.handler = handler;
    }

    @Override
    public void emitCode(CompilationResultBuilder crb, SPARCMacroAssembler masm) {
        leaveFrame(crb);

        // SPARCHotSpotBackend backend = (SPARCHotSpotBackend)
        // HotSpotGraalRuntime.runtime().getBackend();
        // final boolean isStub = true;
        // HotSpotFrameContext frameContext = backend.new HotSpotFrameContext(isStub);
        // frameContext.enter(crb);

        try (ScratchRegister sc = masm.getScratchRegister()) {
            Register scratch = sc.getRegister();
            SPARCCall.indirectJmp(crb, masm, scratch, handler);
        }

        // frameContext.leave(crb);
    }
}
