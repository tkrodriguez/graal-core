/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.graal.lir.aarch64;

import jdk.vm.ci.aarch64.AArch64Kind;
import jdk.vm.ci.meta.Value;

import com.oracle.graal.lir.Variable;
import com.oracle.graal.lir.gen.ArithmeticLIRGeneratorTool;

/**
 * This interface can be used to generate AArch64 LIR for arithmetic operations.
 */
public interface AArch64ArithmeticLIRGeneratorTool extends ArithmeticLIRGeneratorTool {

    Value emitMathLog(Value input, boolean base10);

    Value emitMathCos(Value input);

    Value emitMathSin(Value input);

    Value emitMathTan(Value input);

    Value emitCountLeadingZeros(Value value);

    Value emitCountTrailingZeros(Value value);

    void emitCompareOp(AArch64Kind cmpKind, Variable left, Value right);
}
