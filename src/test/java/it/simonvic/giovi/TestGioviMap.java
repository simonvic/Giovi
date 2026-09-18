package it.simonvic.giovi;

import static it.simonvic.giovi.GioviVisitorUtils.visit;
import static it.simonvic.giovi.GioviVisitorUtils.visitExpression;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import giovi.GioviParserVisitResult;
import giovi.lang.*;
import it.simonvic.giovi.exceptions.GioviException;
import it.simonvic.giovi.exceptions.KeyNotPresent;
import it.simonvic.giovi.exceptions.OperatorNotSupported;
import it.simonvic.giovi.util.GioviLists;
import it.simonvic.giovi.util.GioviMaps;
import it.simonvic.giovi.util.GioviRecords;

public class TestGioviMap {

	//////////////////////////////////////////////////////////////////////////////////////////////// CREATIONS

	@Nested
	class Creation {

		@Test
		void mapOfIntText_shouldHaveIntKey_and_shouldHaveTextElement() {
			var map = visitExpression("""
				[ 1=>"a" 2=>"b" 3=>"c" ]
				""").asGioviMap();
			assertAll(
				() -> assertThat(map.getKeyType().value())
					.isEqualTo(GioviInt.class),
				() -> assertThat(map.getElementType().value())
					.isEqualTo(GioviText.class)
			);
		}

		@Test
		void mapOfMixedKeyType_shouldInferWidestKeyType() {
			assertThat(
				visitExpression("""
					[ 1i=>"a" 2n=>"b" 3r=>"c" ]
					""")
					.asGioviMap()
					.getKeyType()
					.value()
			).isEqualTo(GioviReal.class);
		}

		@Test
		void mapOfMixedElementType_shouldInferWidestElementType() {
			assertThat(
				visitExpression("""
					[ 1=>1n 2=>2i 3=>3r ]
					""")
					.asGioviMap()
					.getElementType()
					.value()
			).isEqualTo(GioviReal.class);
		}

		@Test
		void mapOfMixedKeyElementType_shouldInferWidestKeyElementType() {
			var map = visitExpression("""
					[ 1n=>1n 2i=>2i 3r=>3r ]
					""").asGioviMap();
			assertAll(
				() -> assertThat(map.getKeyType().value()).isEqualTo(GioviReal.class),
				() -> assertThat(map.getElementType().value()).isEqualTo(GioviReal.class)
			);
		}

		@Test
		void mapOfMixedKeyElementType_shouldCastEntriesToWidest() {
			assertThat(
				visitExpression("""
					[ 1n=>1n 2i=>2i 3r=>3r ]
					""")
					.asGioviMap()
					.value()
			).allSatisfy(
				(k, e) -> assertAll(
					() -> assertThat(k.getGioviType()).isEqualTo(GioviType.REAL),
					() -> assertThat(e.getGioviType()).isEqualTo(GioviType.REAL)
				)
			);

		}

		@Test
		void mapOfMixedNonCompatibleElementType_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> visitExpression("""
					[ 1=>1 2=>2 3=>false ]
					"""));
		}

		@Test
		void mapOfMixedNonCompatibleKeyType_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> visitExpression("""
					[ 1=>1 2=>2 false=>3 ]
					"""));
		}

		@Test
		void mapWithElementOfCompatibleLists() {
			assertThat(
				visitExpression("""
					[
						1 => ["a" "b" "c"]
						2 => ["foo" "bar"]
						3 => ["foobar"]
					]
					""")
			).isEqualTo(
				GioviMaps.of(
					1, List.of("a", "b", "c"),
					2, List.of("foo", "bar"),
					3, List.of("foobar")
				)
			);
		}

		@Test
		void mapWithElementOfMixedNonCompatibleLists_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> visitExpression("""
					[
						1 => ["a" "b" "c"]
						2 => [1 2 3]
						3 => [true false true]
					]
					"""));
		}

		@Test
		void mapWithElementOfListOfDifferentButCompatibleTypes_shouldInferWidestElementType() {
			assertThat(
				visitExpression("""
					[
						1 => [1n 2n]
						2 => [69i 420i]
						3 => [6.9 4.20]
					]
					""")
					.asGioviMap()
					.getElementType()
			).isEqualTo(GioviType.LIST_OF_REAL);
		}

		@Test
		void mapWithElementOfListOfDifferentIncompatibleTypes_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> visitExpression("""
					[
						1 => [1n 2n]
						2 => [69i 420i]
						3 => ["a" "b"]
					]
					"""));
		}

		@Test
		void mapWithElementOfListOfDifferentIncompatibleTypes_shouldThrow2() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> visitExpression("""
					[
						1 => ["a" "b"]
						2 => [69i 420i]
						3 => [true false]
					]
					"""));
		}

		@Test
		void mapWithElementOfMap() {
			assertThat(
				visitExpression("""
					[
						1 => ["a"=>true "b"=>true]
						2 => ["b"=>false]
						3 => ["c"=>true]
					]
					""")
			).isEqualTo(
				GioviMaps.of(
					1, Map.of("a", true, "b", true),
					2, Map.of("b", false),
					3, Map.of("c", true)
				)
			);
		}

		@Test
		void mapWithElementOfIncompatibleMaps_shouldThrow() {
			assertThatExceptionOfType(GioviException.class)
				.isThrownBy(() -> visitExpression("""
					[
						1 => ["a"=>true "b"=>false]
						2 => ["a"=>69]
						3 => ["a"=>"a"]
					]
					"""));
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////// OPERATIONS

	@Nested
	class Operations {

		//////////////////////////////////////////////////////////////////////////////////////////// redirection

		@Nested
		class Redirection {

			@Test
			void redir_shouldChangeValue() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						[69=>"foo" 420=>"bar" 69420=>"foobar"] -> x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(69, "foo", 420, "bar", 69420, "foobar"));
			}

			@Test
			void redir_wideneableMap_shouldWiden() {
				assertThat(
					visit("""
						[1r=>1r 2r=>2r 3r=>3r] -> x [Real=>Real]
						[1=>10 2=>20 3=>30] -> x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1d, 10d, 2d, 20d, 3d, 30d));
			}

			@Test
			void redir_nonWideneableMap_shouldThrow() {
				assertThatExceptionOfType(OperatorNotSupported.class)
					.isThrownBy(() -> visit("""
						x [Int=>Int]
						[1r=>1r] -> x
						"""));
			}

			@Test
			void redirPlus_pair_shouldPutPair() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						[3=>"foobar"] ->+ x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "foobar"));
			}

			@Test
			void redirPlus_nonExistingPair_shouldPutPair() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						[69=>"foobar"] ->+ x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "c", 69, "foobar"));
			}

			@Test
			void redirPlus_multiplePairs_shouldPutAllPairs() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						[3=>"foo" 69=>"bar" 42=>"foobar"] ->+ x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "foo", 69, "bar", 42, "foobar"));
			}

			@Test
			void redirPlus_wideneableMap_shouldWiden() {
				assertThat(
					visit("""
						[1r=>1r 2r=>2r 3r=>3r] -> x [Real=>Real]
						[1=>69 2=>420] ->+ x
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1d, 69d, 2d, 420d, 3d, 3d));
			}

			@Test
			void redirPlus_nonWideneableMap_shouldThrow() {
				assertThatExceptionOfType(OperatorNotSupported.class)
					.isThrownBy(() -> visit("""
						[1=>1 2=>2 3=>3] -> x [Int=>Int]
						[1r=>69r 2r=>420r] ->+ x
						"""));
			}

			@Test
			void redirMinus_existingPair_shouldRemovePair() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						x -<- [2=>"b"]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a", 3, "c"));
			}

			@Test
			void redirMinus_pairWithExistingKeyButWrongValue_shouldNotRemovePair() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						x -<- [2=>"foobar"]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "c"));
			}

			@Test
			void redirMinus_pairWithNonExistingKey_shouldNotRemovePair() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						x -<- [222=>"b"]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "c"));
			}

			@Test
			void redirMinus_existingKey_shouldRemovePair() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						x -<- 2
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a", 3, "c"));
			}

			@Test
			void redirMinus_existingNarrowerKey_shouldRemovePair() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						x -<- 2n
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a", 3, "c"));
			}

			@Test
			void redirMinus_nonExistingKey_shouldNotRemovePair() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						x -<- 222
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a", 2, "b", 3, "c"));
			}

			@Test
			void redirMinus_multipleExistingPairs_shouldRemoveAllPairs() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						x -<- [2=>"b" 3=>"c"]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a"));
			}

			@Test
			void redirMinus_multipleExistingNarrowerPairs_shouldRemoveAllPairs() {
				assertThat(
					visit("""
						[1i=>1r 2i=>2r 3i=>3r] -> x [Int=>Real]
						x -<- [2n=>2i]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, 1d, 3, 3d));
			}

			@Test
			@Disabled("Candidate for removal")
			void redirMinus_multipleKeys_shouldRemoveAllKeys() {
				assertThat(
					visit("""
						[1=>"a" 2=>"b" 3=>"c"] -> x [Int=>Text]
						x -<- [2 3]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(GioviMaps.of(1, "a"));
			}

			@Test
			void redirMinus_mapAsKey() {
				assertThat(
					visit("""
						x [[Int=>Text]=>Text] <- [
							[1=>"a"] => "A"
							[2=>"b"] => "B"
							[3=>"c"] => "C"
						]
						x -<- [3=>"c"]
						""")
						.getCurrentScope()
						.getVariable("x")
						.orElseThrow()
						.unwrap()
				).isEqualTo(
					GioviMaps.of(
						Map.of(1, "a"), "A",
						Map.of(2, "b"), "B"
					)
				);
			}

			@Test
			void redirMinus_mapButDifferentFromKeyType_shouldThrow() {
				assertThatExceptionOfType(GioviException.class).isThrownBy(() -> visit("""
					x [[Int=>Text]=>Text] <- [
						[1=>"a"] => "A"
						[2=>"b"] => "B"
						[3=>"c"] => "C"
					]
					x -<- [3=>69]
					"""));
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// arithmetic

		@Nested
		class Arithmetic {

			@Test
			void plus_map_shouldMerge() {
				assertThat(visitExpression("""
					["a"=>1 "b"=>2 "c"=>3] + ["b"=>69 "d"=>420]
					"""))
					.isEqualTo(
						GioviMaps.of(
							"a", 1,
							"b", 69,
							"c", 3,
							"d", 420
						)
					);
			}

			@Test
			void plus_narrowerMap_shouldMerge() {
				assertThat(visitExpression("""
					[1i=>1r 2i=>2r 3i=>3r] + [2n=>69i 4n=>420i]
					"""))
					.isEqualTo(
						GioviMaps.of(
							1, 1d,
							2, 69d,
							3, 3d,
							4, 420d
						)
					);
			}

			@Disabled("Clarify requirements")
			@Test
			void plus_widerMap_shouldMerge() {
				assertThat(visitExpression("""
					[1n=>1i 2n=>2i 3n=>3i] + [2i=>69r 4i=>420r]
					"""))
					.isEqualTo(
						GioviMaps.of(
							1, 1d,
							2, 69d,
							3, 3d,
							4, 420d
						)
					);
			}

			@Test
			void minus_map_shouldIntersecate() {
				assertThat(visitExpression("""
					["a"=>1 "b"=>2 "c"=>3] - ["b"=>2 "d"=>420]
					"""))
					.isEqualTo(
						GioviMaps.of(
							"a", 1,
							"c", 3
						)
					);
			}

			@Test
			void minus_narrowerMap_shouldIntersecate() {
				assertThat(visitExpression("""
					[1i=>1r 2i=>2r 3i=>3r] - [2n=>2i 4n=>4i]
					"""))
					.isEqualTo(
						GioviMaps.of(
							1, 1d,
							3, 3d
						)
					);
			}

			@Disabled("Clarify requirements")
			@Test
			void minus_widerMap_shouldIntersecate() {
				assertThat(visitExpression("""
					[1n=>1i 2n=>2i 3n=>3i] - [2i=>2r 4i=>4r]
					"""))
					.isEqualTo(
						GioviMaps.of(
							1, 1d,
							3, 3d
						)
					);
			}

			@Test
			void minus_key_shouldRemove() {
				assertThat(visitExpression("""
					["a"=>1 "b"=>2 "c"=>3] - "b"
					"""))
					.isEqualTo(
						GioviMaps.of(
							"a", 1,
							"c", 3
						)
					);
			}

			@Test
			void minus_narrowerKey_shouldRemove() {
				assertThat(visitExpression("""
					[1r=>1 2r=>2 3r=>3] - 2n
					"""))
					.isEqualTo(
						GioviMaps.of(
							1d, 1,
							3d, 3
						)
					);
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// boolean

		//////////////////////////////////////////////////////////////////////////////////////////// relational

		@Nested
		class Relational {

			@Test
			void checkingIfContainsExistingKey_shouldReturnTrue() {
				assertThat(visitExpression("""
					["a"=>1 "b"=>2] =~ "a"
					""").asGioviBool().value())
					.isTrue();
			}

			@Test
			void checkingIfContainsExistingEqualKeyWithDifferentType_shouldReturnTrue() {
				assertThat(visitExpression("""
					[1n=>"a" 2n=>"b"] =~ 1i
					""").asGioviBool().value())
					.isTrue();
			}

			@Test
			void checkingIfContainsNonExistingKey_shouldReturnFalse() {
				assertThat(visitExpression("""
					["a"=>1 "b"=>2] =~ "zzz"
					""").asGioviBool().value())
					.isFalse();
			}

			@Test
			void checkingIfContainsExistingSubset_shouldReturnTrue() {
				assertThat(visitExpression("""
					["a"=>1 "b"=>2 "c"=>3] =~ ["b"=>2 "c"=>3]
					""").asGioviBool().value())
					.isTrue();
			}

		}

		@Nested
		class Equality {

			@Test
			void comparingMapsWithEqualEntries_shouldReturnTrue() {
				assertThat(visitExpression("""
					["a"=>1i] = ["a"=>1i]
					""").asGioviBool().value())
					.isTrue();
			}

			@Test
			void comparingMapsWithDifferentEntryTypeButEqualValue_shouldReturnTrue() {
				assertThat(visitExpression("""
					[1n=>1n] = [1i=>1i]
					""").asGioviBool().value())
					.isTrue();
			}

			@Test
			void comparingMapsWithDifferentKeyType_shouldReturnFalse() {
				assertThat(visitExpression("""
					[1=>69] = ["hello"=>69]
					""").asGioviBool().value())
					.isFalse();
			}

			@Test
			void comparingMapsWithDifferentElementType_shouldReturnFalse() {
				assertThat(visitExpression("""
					[1=>"hello"] = [1=>69]
					""").asGioviBool().value())
					.isFalse();
			}

			@Test
			void comparingMapsWithDifferentElements_shouldReturnFalse() {
				assertThat(visitExpression("""
					["a"=>1 "b"=>2] = ["a"=>1 "b"=>3]
					""").asGioviBool().value())
					.isFalse();
			}

			@Test
			void comparingMapsWithDifferentKeys_shouldReturnFalse() {
				assertThat(visitExpression("""
					["a"=>1 "c"=>2] = ["a"=>1 "b"=>2]
					""").asGioviBool().value())
					.isFalse();
			}

			@Test
			void comparingMapsWithDifferentEntriesCount_shouldReturnFalse() {
				assertThat(visitExpression("""
					["a"=>1] = ["a"=>1 "b"=>2]
					""").asGioviBool().value())
					.isFalse();
			}

		}

		//////////////////////////////////////////////////////////////////////////////////////////// access

		@Nested
		class Access {

			@Test
			void accessWithLengthOperator_shouldReturnEntriesCount() {
				assertThat(visitExpression("""
					[1=>"a" 2=>"b" 3=>"c"].%
					""").asGioviInt().value())
					.isEqualTo(3);
			}

			@Test
			void accessWithIndexesOperator_shouldReturnListOfKeys() {
				assertThat(visitExpression("""
					[1=>"a" 2=>"b" 3=>"c"].@
					"""))
					.isEqualTo(GioviLists.of(1, 2, 3));
			}

			@Test
			void accessWithElementsOperator_shouldReturnListOfElements() {
				assertThat(visitExpression("""
					[1=>"a" 2=>"b" 3=>"c"].*
					"""))
					.isEqualTo(GioviLists.of("a", "b", "c"));
			}

			@Test
			void accessWithValueKey_shouldReturnMappedElement() {
				assertThat(
					visitExpression("""
						[1=>"a" 2=>"b" 3=>"c"].2
						""")
						.unwrapAsGioviValue()
						.value()
				).isEqualTo("b");
			}

			@Test
			void accessWithNotExistingValueKey_shouldThrow() {
				assertThatExceptionOfType(KeyNotPresent.class)
					.isThrownBy(() -> visitExpression("""
						[1=>"a" 2=>"b" 3=>"c"].69
						"""));
			}

		}

	}

	@Nested
	class NativeBindings {


		@ParameterizedTest
		@CsvSource(textBlock = """
			[=>].count(), 0
			["a"=>1].count(), 1
			["a"=>1 "b"=>2 "c"=>3].count(), 3
			"""
		)
		void test_count(String code, int expected) {
			assertThat(visitExpression(code))
				.isEqualTo(new GioviInt(expected));
		}

		@ParameterizedTest
		@CsvSource(textBlock = """
			[=>].isEmpty(), true
			["a"=>1].isEmpty(), false
			"""
		)
		void test_isEmpty(String code, boolean expected) {
			assertThat(visitExpression(code))
				.isEqualTo(GioviBool.of(expected));
		}

		@Test
		void test_keys() {
			assertThat(
				visitExpression("""
					["a"=>0 "b"=>1 "c"=>2].keys()
					""")
			).isEqualTo(
				GioviLists.of(
					"a", "b", "c"
				)
			);
		}

		@Test
		void test_values() {
			assertThat(
				visitExpression("""
					["a"=>0 "b"=>1 "c"=>2].values()
					""")
			).isEqualTo(
				GioviLists.of(
					0, 1, 2
				)
			);
		}

		@Test
		void testThat_put_returnsSameInstance() {
			var visitor = visit("""
				m [Text=>Int] <- ["a"=>0 "b"=>1 "c"=>2]
				""");
			var m = visitor.lookup("m").orElseThrow().unwrapAsGioviValue();
			assertThat(visitExpression(visitor, """
				m.put("foo", 69)
				"""))
				.isSameAs(m);
		}

		@Test
		void test_put() {
			var visitor = visit("""
				m [Text=>Int] <- ["a"=>0 "b"=>1 "c"=>2]
				m.put("b", 69)
				""");
			assertThat(visitor.lookup("m"))
				.<GioviValue<?>>map(GioviParserVisitResult::unwrapAsGioviValue)
				.contains(GioviMaps.of("a", 0, "b", 69, "c", 2));
		}

		@Test
		void test_replace() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				l.replace(index=1, element=69)
				""");
			assertThat(visitor.lookup("l"))
				.<GioviValue<?>>map(GioviParserVisitResult::unwrapAsGioviValue)
				.contains(GioviLists.of(0, 69, 2, 3, 4));
		}

		@Test
		void test_remove() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				l.remove(1)
				""");
			assertThat(visitor.lookup("l"))
				.<GioviValue<?>>map(GioviParserVisitResult::unwrapAsGioviValue)
				.contains(GioviLists.of(0, 2, 3, 4));
		}

		@Test
		void test_reverse() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				l.reverse()
				""");
			assertThat(visitor.lookup("l"))
				.<GioviValue<?>>map(GioviParserVisitResult::unwrapAsGioviValue)
				.contains(GioviLists.of(4, 3, 2, 1, 0));
		}

		@Test
		void test_reversed() {
			var visitor = visit("""
				l [Int] <- [0 1 2 3 4]
				""");
			var l = visitor.lookup("l").orElseThrow().unwrapAsGioviValue();
			assertThat(visitExpression(visitor, """
				l.reversed()
				"""))
				.isNotSameAs(l)
				.isEqualTo(GioviLists.of(4, 3, 2, 1, 0));
		}

		@Test
		void test_find() {
			var visitor = visit("""
				l [Real] <- [0r 1r 2r 3r 4r]
				""");
			assertThat(visitExpression(visitor, """
				l.find(2n)
				"""))
				.isEqualTo(
					GioviRecords.of(
						"found", true,
						"index", 2
					)
				);
		}

		@Test
		void testThat_find_notEqualElement_returnsFalse() {
			var visitor = visit("""
				l [Real] <- [0r 1r 2r 3r 4r]
				""");
			assertThat(visitExpression(visitor, """
				l.find(69)
				"""))
				.isEqualTo(
					GioviRecords.of(
						"found", false,
						"index", 0
					)
				);
		}

	}

}
