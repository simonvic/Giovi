package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitor.runExpression;
import static it.simonvic.giovi.GioviVisitor.visit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.KeyNotPresent;
import it.simonvic.giovi.lang.values.GioviInt;
import it.simonvic.giovi.lang.values.GioviReal;
import it.simonvic.giovi.lang.values.GioviText;
import it.simonvic.giovi.lang.values.natives.Type;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviMaps;

public class TestGioviMap {

	//                                                                                              Creation
	@Nested
	class Creation {

		@Test
		void mapOfIntText_shouldHaveIntKey_and_shouldHaveTextElement() {
			var map = runExpression("""
				[ 1:"a" 2:"b" 3:"c" ]
				""").asGioviMap();
			assertAll(
				() -> assertThat(map.getKeyType().unbox().getClazz())
					.isEqualTo(GioviInt.class),
				() -> assertThat(map.getElementType().unbox().getClazz())
					.isEqualTo(GioviText.class));
		}

		@Test
		void mapOfMixedKeyType_shouldInferHighestKeyType() {
			assertThat(runExpression("""
				[ 1n:"a" 2i:"b" 3r:"c" ]
				""").asGioviMap()
				.getKeyType().unbox().getClazz())
					.isEqualTo(GioviReal.class);
		}

		@Test
		void mapOfMixedElementType_shouldInferHighestElementType() {
			assertThat(runExpression("""
				[ 1:1n 2:2i 3:3r ]
				""").asGioviMap()
				.getElementType().unbox().getClazz())
					.isEqualTo(GioviReal.class);
		}

		@Test
		void mapOfMixedKeyElementType_shouldInferHighestKeyElementType() {
			var map = runExpression("""
				[ 1n:1n 2i:2i 3r:3r ]
				""").asGioviMap();
			assertAll(
				() -> assertThat(map
					.getKeyType().unbox().getClazz())
						.isEqualTo(GioviReal.class),
				() -> assertThat(map
					.getElementType().unbox().getClazz())
						.isEqualTo(GioviReal.class));

		}

		@Test
		void mapOfMixedNonCompatibleElementType_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> runExpression("""
					[ 1:1 2:2 3:false ]
					"""));
		}

		@Test
		void mapOfMixedNonCompatibleKeyType_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> runExpression("""
					[ 1:1 2:2 false:3 ]
					"""));
		}

		@Test
		void mapWithElementOfCompatibleLists() {
			assertThat(runExpression("""
				[
					1 : ["a" "b" "c"]
					2 : ["foo" "bar"]
					3 : ["foobar"]
				]
				"""))
				.isEqualTo(GioviMaps.of(
					1, List.of("a", "b", "c"),
					2, List.of("foo", "bar"),
					3, List.of("foobar")));
		}

		@Test
		void mapWithElementOfMixedNonCompatibleLists_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> runExpression("""
					[
						1 : ["a" "b" "c"]
						2 : [1 2 3]
						3 : [true false true]
					]
					"""));
		}

		@Test
		void mapWithElementOfListOfDifferentButCompatibleTypes_shouldInferHighestElementType() {
			assertThat(runExpression("""
				[
					1 : [1n 2n]
					2 : [69i 420i]
					3 : [6.9 4.20]
				]
				""").asGioviMap()
				.getElementType().unbox())
					.isEqualTo(new Type.List(new Type.Primitive<>(GioviReal.class)));
		}

		@Test
		void mapWithElementOfListOfDifferentIncompatibleTypes_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> runExpression("""
					[
						1 : [1n 2n]
						2 : [69i 420i]
						3 : ["a" "b"]
					]
					"""));
		}

		@Test
		void mapWithElementOfListOfDifferentIncompatibleTypes_shouldThrow2() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> runExpression("""
					[
						1 : ["a" "b"]
						2 : [69i 420i]
						3 : [true false]
					]
					"""));
		}

		@Test
		void mapWithElementOfMap() {
			assertThat(runExpression("""
				[
					1: ["a":true "b":true]
					2: ["b":false]
					3: ["c":true]
				]
				"""))
				.isEqualTo(GioviMaps.of(
					1, Map.of("a", true, "b", true),
					2, Map.of("b", false),
					3, Map.of("c", true)));
		}

		@Test
		void mapWithElementOfIncompatibleMaps_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> runExpression("""
					[
						1: ["a":true "b":false]
						2: ["a":69]
						3: ["a":"a"]
					]
					"""));
		}

		// TODO(test): structs and tuples

	}

	//                                                                                              Operations
	@Nested
	class Operations {

		//                                                                                          redirection
		@Nested
		class Redirection {

			@Test
			void redir_shouldChangeValue() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					[69:"foo" 420:"bar" 69420:"foobar"] -> x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(69, "foo", 420, "bar", 69420, "foobar"));
			}

			@Test
			void redirPlus_pair_shouldPutPair() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					[3:"foobar"] ->+ x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "foobar"));
			}

			@Test
			void redirPlus_nonExistingPair_shouldPutPair() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					[69:"foobar"] ->+ x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "c", 69, "foobar"));
			}

			@Test
			void redirPlus_multiplePairs_shouldPutAllPairs() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					[3:"foo" 69:"bar" 42:"foobar"] ->+ x
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "foo", 69, "bar", 42, "foobar"));
			}

			@Test
			void redirMinus_existingPair_shouldRemovePair() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					x -<- [2:"b"]
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(1, "a", 3, "c"));
			}

			@Test
			void redirMinus_pairWithExistingKeyButWrongValue_shouldNotRemovePair() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					x -<- [2:"foobar"]
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "c"));
			}

			@Test
			void redirMinus_pairWithNonExistingKey_shouldNotRemovePair() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					x -<- [222:"b"]
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "c"));
			}

			@Test
			void redirMinus_existingKey_shouldRemovePair() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					x -<- 2
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(1, "a", 3, "c"));
			}

			@Test
			void redirMinus_nonExistingKey_shouldNotRemovePair() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					x -<- 222
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "c"));
			}

			@Test
			void redirMinus_multipleExistingPairs_shouldRemoveAllPairs() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					x -<- [2:"b" 3:"c"]
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(1, "a"));
			}

			@Test
			@Disabled("Candidate for removal")
			void redirMinus_multipleKeys_shouldRemoveAllKeys() {
				assertThat(visit("""
					[1:"a" 2:"b" 3:"c"] -> x [Int:Text]
					x -<- [2 3]
					""").getCurrentScope()
					.getVariableValue("x"))
						.isEqualTo(GioviMaps.of(1, "a"));
			}

			// TODO(test): map with map as key or element
		}

		//                                                                                          arithmetic
		@Nested
		@Disabled("Candidate not implemented yet")
		class Arithmetic {
		}

		//                                                                                          boolean
		//                                                                                          relational
		@Nested
		class Relational {

			@Test
			void checkingExistingKey_shouldReturnTrue() {
				assertThat(runExpression("""
					["a":1 "b":2] =~ "a"
					""").asGioviBool().unbox())
					.isTrue();
			}

			@Test
			void checkingNonExistingKey_shouldReturnFalse() {
				assertThat(runExpression("""
					["a":1 "b":2] =~ "zzz"
					""").asGioviBool().unbox())
					.isFalse();
			}

			@Test
			@Disabled("Candidate not implemented yet")
			void checkingExistingSubset_shouldReturnTrue() {
				assertThat(runExpression("""
					["a":1 "b":2 "c":3] =~ ["b":2 "c":3]
					""").asGioviBool().unbox())
					.isTrue();
			}

		}

		@Nested
		class Equality {

			@Test
			void comparingMapsWithEqualEntries_shouldReturnTrue() {
				assertThat(runExpression("""
					["a":1i] = ["a":1i]
					""").asGioviBool().unbox())
					.isTrue();
			}

			@Test
			void comparingMapsWithElementsWithDifferentTypeButEqualValue_shouldReturnTrue() {
				assertThat(runExpression("""
					["a":1n] = ["a":1i]
					""").asGioviBool().unbox())
					.isTrue();
			}

			@Test
			void comparingMapsWithDifferentElements_shouldReturnFalse() {
				assertThat(runExpression("""
					["a":1 "b":2] = ["a":1 "b":3]
					""").asGioviBool().unbox())
					.isFalse();
			}

			@Test
			void comparingMapsWithDifferentKeys_shouldReturnFalse() {
				assertThat(runExpression("""
					["a":1 "c":2] = ["a":1 "b":2]
					""").asGioviBool().unbox())
					.isFalse();
			}

			@Test
			void comparingMapsWithDifferentEntriesCount_shouldReturnFalse() {
				assertThat(runExpression("""
					["a":1] = ["a":1 "b":2]
					""").asGioviBool().unbox())
					.isFalse();
			}

		}

		//                                                                                          access
		@Nested
		class Access {

			@Test
			void accessWithLengthOperator_shouldReturnEntriesCount() {
				assertThat(runExpression("""
					[1:"a" 2:"b" 3:"c"]@%
					""").asGioviInt().unbox())
					.isEqualTo(3);
			}

			@Test
			void accessWithIndexesOperator_shouldReturnListOfKeys() {
				assertThat(runExpression("""
					[1:"a" 2:"b" 3:"c"]@@
					"""))
					.isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void accessWithElementsOperator_shouldReturnListOfElements() {
				assertThat(runExpression("""
					[1:"a" 2:"b" 3:"c"]@*
					"""))
					.isEqualTo(GioviLists.of("a", "b", "c"));
			}

			@Test
			void accessWithValueKey_shouldReturnMappedElement() {
				assertThat(runExpression("""
					[1:"a" 2:"b" 3:"c"]@2
					""").asGioviText().unbox())
					.isEqualTo("b");
			}

			@Test
			void accessWithNotExistingValueKey_shouldThrow() {
				assertThatExceptionOfType(KeyNotPresent.class)
					.isThrownBy(() -> runExpression("""
						[1:"a" 2:"b" 3:"c"]@69
						"""));
			}

		}

	}


}
