 .data
x:
  .word 1
y:
  .word 2
z:
  .word 3
a:
  .word 4
b:
  .word 5
c:
  .word 6
d:
  .word 7
e:
  .word 8
f:
  .word 9
g:
  .word 10
h:
  .word 11
i:
  .word 12
j:
  .word 13
k:
  .word 14
l:
  .word 15
m:
  .word 16
n:
  .word 17
o:
  .word 18
p:
  .word 19
q:
  .word 20
 .text
 .globl main
main:
  addi sp, sp, -720
mainEntry:
  li t0, 1
  mv t3, t0
  li t0, 2
  mv t4, t0
  li t0, 3
  mv t5, t0
  li t0, 4
  mv t6, t0
  li t0, 5
  mv a1, t0
  li t0, 6
  mv a2, t0
  li t0, 7
  mv a3, t0
  li t0, 8
  mv a4, t0
  li t0, 9
  mv a5, t0
  li t0, 10
  mv a6, t0
  li t0, 11
  mv a7, t0
  li t0, 12
  mv s0, t0
  li t0, 13
  mv s1, t0
  li t0, 14
  mv s2, t0
  li t0, 15
  mv s3, t0
  li t0, 16
  mv s4, t0
  li t0, 17
  mv s5, t0
  li t0, 18
  mv s6, t0
  li t0, 19
  mv s7, t0
  li t0, 20
  mv s8, t0
  la t2, x
  lw t0, 0(t2)
  mv s9, t0
  li t0, 1
  add s10, s9, t0
  la t2, x
  sw s10, 0(t2)
  la t2, y
  lw t0, 0(t2)
  mv s11, t0
  li t0, 2
  add s9, s11, t0
  la t2, y
  sw s9, 0(t2)
  la t2, z
  lw t0, 0(t2)
  mv s10, t0
  li t0, 3
  add s11, s10, t0
  la t2, z
  sw s11, 0(t2)
  la t2, a
  lw t0, 0(t2)
  mv s9, t0
  li t0, 4
  add s10, s9, t0
  la t2, a
  sw s10, 0(t2)
  la t2, b
  lw t0, 0(t2)
  mv s11, t0
  li t0, 5
  add s9, s11, t0
  la t2, b
  sw s9, 0(t2)
  la t2, c
  lw t0, 0(t2)
  mv s10, t0
  li t0, 6
  add s11, s10, t0
  la t2, c
  sw s11, 0(t2)
  la t2, d
  lw t0, 0(t2)
  mv s9, t0
  li t0, 7
  add s10, s9, t0
  la t2, d
  sw s10, 0(t2)
  la t2, e
  lw t0, 0(t2)
  mv s11, t0
  li t0, 8
  add s9, s11, t0
  la t2, e
  sw s9, 0(t2)
  la t2, f
  lw t0, 0(t2)
  mv s10, t0
  li t0, 9
  add s11, s10, t0
  la t2, f
  sw s11, 0(t2)
  la t2, g
  lw t0, 0(t2)
  mv s9, t0
  li t0, 10
  add s10, s9, t0
  la t2, g
  sw s10, 0(t2)
  la t2, h
  lw t0, 0(t2)
  mv s11, t0
  li t0, 11
  add s9, s11, t0
  la t2, h
  sw s9, 0(t2)
  la t2, i
  lw t0, 0(t2)
  mv s10, t0
  li t0, 12
  add s11, s10, t0
  la t2, i
  sw s11, 0(t2)
  la t2, j
  lw t0, 0(t2)
  mv s9, t0
  li t0, 13
  add s10, s9, t0
  la t2, j
  sw s10, 0(t2)
  la t2, k
  lw t0, 0(t2)
  mv s11, t0
  li t0, 14
  add s9, s11, t0
  la t2, k
  sw s9, 0(t2)
  la t2, l
  lw t0, 0(t2)
  mv s10, t0
  li t0, 15
  add s11, s10, t0
  la t2, l
  sw s11, 0(t2)
  la t2, m
  lw t0, 0(t2)
  mv s9, t0
  li t0, 16
  add s10, s9, t0
  la t2, m
  sw s10, 0(t2)
  la t2, n
  lw t0, 0(t2)
  mv s11, t0
  li t0, 17
  add s9, s11, t0
  la t2, n
  sw s9, 0(t2)
  la t2, o
  lw t0, 0(t2)
  mv s10, t0
  li t0, 18
  add s11, s10, t0
  la t2, o
  sw s11, 0(t2)
  la t2, p
  lw t0, 0(t2)
  mv s9, t0
  li t0, 19
  add s10, s9, t0
  la t2, p
  sw s10, 0(t2)
  la t2, q
  lw t0, 0(t2)
  mv s11, t0
  li t0, 20
  add s9, s11, t0
  la t2, q
  sw s9, 0(t2)
  mv s10, t3
  li t0, 2
  mul s11, s10, t0
  mv t3, s11
  mv s9, t4
  li t0, 2
  mul s10, s9, t0
  mv t4, s10
  mv s11, t5
  li t0, 2
  mul s9, s11, t0
  mv t5, s9
  mv s10, t6
  li t0, 2
  mul s11, s10, t0
  mv t6, s11
  mv s9, a1
  li t0, 2
  mul s10, s9, t0
  mv a1, s10
  mv s11, a2
  li t0, 2
  mul s9, s11, t0
  mv a2, s9
  mv s10, a3
  li t0, 2
  mul s11, s10, t0
  mv a3, s11
  mv s9, a4
  li t0, 2
  mul s10, s9, t0
  mv a4, s10
  mv s11, a5
  li t0, 2
  mul s9, s11, t0
  mv a5, s9
  mv s10, a6
  li t0, 2
  mul s11, s10, t0
  mv a6, s11
  mv s9, a7
  li t0, 2
  mul s10, s9, t0
  mv a7, s10
  mv s11, s0
  li t0, 2
  mul s9, s11, t0
  mv s0, s9
  mv s10, s1
  li t0, 2
  mul s11, s10, t0
  mv s1, s11
  mv s9, s2
  li t0, 2
  mul s10, s9, t0
  mv s2, s10
  mv s11, s3
  li t0, 2
  mul s9, s11, t0
  mv s3, s9
  mv s10, s4
  li t0, 2
  mul s11, s10, t0
  mv s4, s11
  mv s9, s5
  li t0, 2
  mul s10, s9, t0
  mv s5, s10
  mv s11, s6
  li t0, 2
  mul s9, s11, t0
  mv s6, s9
  mv s10, s7
  li t0, 2
  mul s11, s10, t0
  mv s7, s11
  mv s9, s8
  li t0, 2
  mul s10, s9, t0
  mv s8, s10
  mv s11, t3
  mv s9, t4
  add s10, s11, s9
  mv t3, t5
  add t4, s10, t3
  mv s11, t6
  add s9, t4, s11
  mv t5, a1
  add s10, s9, t5
  mv t3, a2
  add t6, s10, t3
  mv t4, a3
  add s11, t6, t4
  mv a1, a4
  add s9, s11, a1
  mv t5, a5
  add a2, s9, t5
  mv s10, a6
  add t3, a2, s10
  mv a3, a7
  add t6, t3, a3
  mv t4, s0
  add a4, t6, t4
  mv s11, s1
  add a1, a4, s11
  mv a5, s2
  add s9, a1, a5
  mv t5, s3
  add a6, s9, t5
  mv a2, s4
  add s10, a6, a2
  mv a7, s5
  add t3, s10, a7
  mv a3, s6
  add s0, t3, a3
  mv t6, s7
  add t4, s0, t6
  mv s1, s8
  add a4, t4, s1
  la t2, x
  lw t0, 0(t2)
  mv s11, t0
  add s2, a4, s11
  la t2, y
  lw t0, 0(t2)
  mv a1, t0
  add a5, s2, a1
  la t2, z
  lw t0, 0(t2)
  mv s3, t0
  add s9, a5, s3
  la t2, a
  lw t0, 0(t2)
  mv t5, t0
  add s4, s9, t5
  la t2, b
  lw t0, 0(t2)
  mv a6, t0
  add a2, s4, a6
  la t2, c
  lw t0, 0(t2)
  mv s5, t0
  add s10, a2, s5
  la t2, d
  lw t0, 0(t2)
  mv a7, t0
  add s6, s10, a7
  la t2, e
  lw t0, 0(t2)
  mv t3, t0
  add a3, s6, t3
  la t2, f
  lw t0, 0(t2)
  mv s7, t0
  add s0, a3, s7
  la t2, g
  lw t0, 0(t2)
  mv t6, t0
  add s8, s0, t6
  la t2, h
  lw t0, 0(t2)
  mv t4, t0
  add s1, s8, t4
  la t2, i
  lw t0, 0(t2)
  mv a4, t0
  add s11, s1, a4
  la t2, j
  lw t0, 0(t2)
  mv s2, t0
  add a1, s11, s2
  la t2, k
  lw t0, 0(t2)
  mv a5, t0
  add s3, a1, a5
  la t2, l
  lw t0, 0(t2)
  mv s9, t0
  add t5, s3, s9
  la t2, m
  lw t0, 0(t2)
  mv s4, t0
  add a6, t5, s4
  la t2, n
  lw t0, 0(t2)
  mv a2, t0
  add s5, a6, a2
  la t2, o
  lw t0, 0(t2)
  mv s10, t0
  add a7, s5, s10
  la t2, p
  lw t0, 0(t2)
  mv s6, t0
  add t3, a7, s6
  la t2, q
  lw t0, 0(t2)
  mv a3, t0
  add s7, t3, a3
  mv a0, s7
  addi sp, sp, 720
  li a7, 93
  ecall

