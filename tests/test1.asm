 .data
a:
  .word 1
 .text
 .globl main
main:
  addi sp, sp, -32
mainEntry:
  la t2, a
  lw t0, 0(t2)
  mv t3, t0
  li t0, 1
  add t4, t3, t0
  mv t5, t4
  mv t6, t5
  mv a1, t5
  add a2, t6, a1
  mv a3, a2
  mv a4, a3
  mv a0, a4
  addi sp, sp, 32
  li a7, 93
  ecall

