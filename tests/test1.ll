; ModuleID = 'module'
source_filename = "module"

@a = global i32 1

define i32 @main() {
mainEntry:
  %loadtmp = load i32, i32* @a, align 4
  %addtmp = add i32 %loadtmp, 1
  %a = alloca i32, align 4
  store i32 %addtmp, i32* %a, align 4
  %loadtmp1 = load i32, i32* %a, align 4
  %loadtmp2 = load i32, i32* %a, align 4
  %addtmp3 = add i32 %loadtmp1, %loadtmp2
  %b = alloca i32, align 4
  store i32 %addtmp3, i32* %b, align 4
  %loadtmp4 = load i32, i32* %b, align 4
  ret i32 %loadtmp4
}
