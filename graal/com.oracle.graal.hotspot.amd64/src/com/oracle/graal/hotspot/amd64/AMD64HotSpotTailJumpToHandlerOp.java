/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.graal.hotspot.amd64;

import com.oracle.graal.asm.amd64.AMD64MacroAssembler;
import com.oracle.graal.compiler.common.spi.ForeignCallLinkage;
import com.oracle.graal.lir.LIRInstructionClass;
import com.oracle.graal.lir.Opcode;
import com.oracle.graal.lir.amd64.AMD64Call;
import com.oracle.graal.lir.asm.CompilationResultBuilder;

/**
 * Removes the current frame and tail calls another routine. Any frame adjustment is undone but the
 * return address is still on the stack.
 */
@Opcode("TAIL_JUMP_TO_HANDLER")
final class AMD64HotSpotTailJumpToHandlerOp extends AMD64HotSpotEpilogueBlockEndOp {

    public static final LIRInstructionClass<AMD64HotSpotTailJumpToHandlerOp> TYPE = LIRInstructionClass.create(AMD64HotSpotTailJumpToHandlerOp.class);
    private final ForeignCallLinkage handler;

    protected AMD64HotSpotTailJumpToHandlerOp(ForeignCallLinkage handler) {
        super(TYPE);
        this.handler = handler;
    }

    @Override
    public void emitCode(CompilationResultBuilder crb, AMD64MacroAssembler masm) {
        leaveFrameAndRestoreRbp(crb, masm);
        AMD64Call.directJmp(crb, masm, handler);
    }
}
