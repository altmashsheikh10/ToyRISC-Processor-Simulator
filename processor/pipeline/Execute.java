package processor.pipeline;

import generic.Instruction;
import generic.Operand;
import generic.Statistics;
import processor.Processor;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;
import java.util.HashSet;
import java.util.Set;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;
	IF_EnableLatchType IF_EnableLatch;

	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch, IF_OF_LatchType iF_OF_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}

	public void performEX()
	{
		if(OF_EX_Latch.isEX_Busy()){
			IF_OF_Latch.setOF_Busy(true);
		}
		else {
			IF_OF_Latch.setOF_Busy(false);

			if(OF_EX_Latch.isEX_Locked()){
				EX_MA_Latch.setMA_Lock(true);
				OF_EX_Latch.setEX_Lock(false);
				EX_MA_Latch.setInstruction(null);
				OF_EX_Latch.setEX_enable(false);
			}
			else if(OF_EX_Latch.isEX_enable()) {

				OF_EX_Latch.setEX_enable(false);
				Instruction currentInstruction = OF_EX_Latch.getInstruction();
				System.out.println("\nEX: " + currentInstruction);
				int currentPC = currentInstruction.getProgramCounter();
				OperationType currentOperation = currentInstruction.getOperationType();
				int sourceOperand1 = -1, sourceOperand2 = -1, immediate, remainder;
				int aluResult = -1;

				//Creating set of branch instructions and end instruction
				Set<String> BranchInstructions = new HashSet<String>();
				//Adding the branch instructions
				BranchInstructions.add("jmp");
				BranchInstructions.add("beq");
				BranchInstructions.add("bne");
				BranchInstructions.add("blt");
				BranchInstructions.add("bgt");
				BranchInstructions.add("end");

				if (BranchInstructions.contains(currentOperation.name())) {
					if (currentOperation != OperationType.end) {
						Statistics.setNumberOfBranchesTaken(Statistics.getNumberOfBranchesTaken() + 1);
					}
					IF_EnableLatch.setIF_enable(false);
					IF_OF_Latch.setOF_enable(false);
					OF_EX_Latch.setEX_enable(false);
				}

				if (currentOperation == OperationType.add) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					aluResult = sourceOperand1 + sourceOperand2;
				} else if (currentOperation == OperationType.addi) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 + immediate;
				} else if (currentOperation == OperationType.sub) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					aluResult = sourceOperand1 - sourceOperand2;
				} else if (currentOperation == OperationType.subi) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 - immediate;
				} else if (currentOperation == OperationType.mul) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					aluResult = sourceOperand1 * sourceOperand2;

				} else if (currentOperation == OperationType.muli) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 * immediate;

				} else if (currentOperation == OperationType.div) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					aluResult = sourceOperand1 / sourceOperand2;
					remainder = (sourceOperand1 % sourceOperand2);
					containingProcessor.getRegisterFile().setValue(31, remainder);

				} else if (currentOperation == OperationType.divi) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 / immediate;
					remainder = (sourceOperand1 % immediate);
					containingProcessor.getRegisterFile().setValue(31, remainder);

				} else if (currentOperation == OperationType.and) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					aluResult = sourceOperand1 & sourceOperand2;

				} else if (currentOperation == OperationType.andi) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 & immediate;

				} else if (currentOperation == OperationType.or) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					aluResult = sourceOperand1 | sourceOperand2;

				} else if (currentOperation == OperationType.ori) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 | immediate;

				} else if (currentOperation == OperationType.xor) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					aluResult = sourceOperand1 ^ sourceOperand2;

				} else if (currentOperation == OperationType.xori) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 ^ immediate;

				} else if (currentOperation == OperationType.slt) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					if (sourceOperand1 < sourceOperand2)
						aluResult = 1;
					else
						aluResult = 0;

				} else if (currentOperation == OperationType.slti) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					if (sourceOperand1 < immediate)
						aluResult = 1;
					else
						aluResult = 0;

				} else if (currentOperation == OperationType.sll) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					aluResult = sourceOperand1 << sourceOperand2;

				} else if (currentOperation == OperationType.slli) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 << immediate;

				} else if (currentOperation == OperationType.srl) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					aluResult = sourceOperand1 >>> sourceOperand2;

				} else if (currentOperation == OperationType.srli) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 >>> immediate;

				} else if (currentOperation == OperationType.sra) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					aluResult = sourceOperand1 >> sourceOperand2;

				} else if (currentOperation == OperationType.srai) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 >> immediate;

				} else if (currentOperation == OperationType.load) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 + immediate;

				} else if (currentOperation == OperationType.store) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getDestinationOperand().getValue());
					immediate = currentInstruction.getSourceOperand2().getValue();
					aluResult = sourceOperand1 + immediate;

				} else if (currentOperation == OperationType.jmp) {
					OperandType jump = currentInstruction.getDestinationOperand().getOperandType();
					if (jump == OperandType.Register) {
						immediate = containingProcessor.getRegisterFile()
								.getValue(currentInstruction.getDestinationOperand().getValue());
					} else {
						immediate = currentInstruction.getDestinationOperand().getValue();
					}
					aluResult = currentPC + immediate;
					EX_IF_Latch.setEX_IF_enable(true, aluResult);

				} else if (currentOperation == OperationType.beq) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					immediate = currentInstruction.getDestinationOperand().getValue();
					if (sourceOperand1 == sourceOperand2) {
						aluResult = currentPC + immediate;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
					}

				} else if (currentOperation == OperationType.bne) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					immediate = currentInstruction.getDestinationOperand().getValue();
					if (sourceOperand1 != sourceOperand2) {
						aluResult = currentPC + immediate;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
					}

				} else if (currentOperation == OperationType.blt) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					immediate = currentInstruction.getDestinationOperand().getValue();
					System.out.println(sourceOperand1 + " " + sourceOperand2);
					if (sourceOperand1 < sourceOperand2) {
						aluResult = currentPC + immediate;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
						System.out.println("aluresult = " + aluResult);
					}

				} else if (currentOperation == OperationType.bgt) {
					sourceOperand1 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile()
							.getValue(currentInstruction.getSourceOperand2().getValue());
					immediate = currentInstruction.getDestinationOperand().getValue();
					if (sourceOperand1 > sourceOperand2) {
						aluResult = currentPC + immediate;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
					}

				} else if (currentOperation == OperationType.end) {

				} else {

				}
				EX_MA_Latch.setAluResult(aluResult);
				EX_MA_Latch.setInstruction(currentInstruction);
				EX_MA_Latch.setMA_enable(true);

				/*if(aluResult != -1)
					System.out.println("\nEX Stage: " + "Current PC:" + currentPC + " rs1:" + sourceOperand1 + " rs2:" + sourceOperand2 + " Alu:" + aluResult);
				else
					System.out.println("\nEX Stage: " + "Current PC:" + currentPC + " rs1:" + sourceOperand1 + " rs2:" + sourceOperand2);
				*/
			}
		}
	}
}
