package Manager;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import DB.BasketlistDAO;
import DB.BasketlistDTO;
import DB.GoodsDAO;
import Member.BasketList;
import Member.Orderlist;

public class ShoppingMall extends JFrame {
	Scanner in = new Scanner(System.in);
	String header[] = { "상품코드", "상품이름", "수량", "가격", "체크" };
	JTabbedPane tabpane = new JTabbedPane();
	DefaultTableModel tablemodel = new DefaultTableModel(header, 0);
	JTable table = new JTable(tablemodel);
	JScrollPane tableScroll = new JScrollPane(table);

	// center panel
	JPanel tab_center = new JPanel();
	JPanel tab_south = new JPanel();
	JPanel south_north = new JPanel();

	// JTextField[] txtfield = new JTextField[5];
	JTextField tfield = null;
	JTextField total = new JTextField(10);

	int modIntRow = -1;
	int chk = 0;
	int num = 0;

	private GoodsDAO gdao = GoodsDAO.getInstance();
	BasketlistDAO dao = BasketlistDAO.getInstance();
	ArrayList<String[]> initList = new ArrayList<>();
	String id = null;
	private BasketlistDTO dto = null;
	ArrayList<String[]> goodsList = new ArrayList<>();
	ArrayList<BasketlistDTO> rlist = new ArrayList<>();

	public ShoppingMall(String id) {
		super("쇼핑몰");// super의 생성자 호출
		this.id = id;
		System.out.println(id);
		Dimension size = new Dimension(600, 400);
		createpanel();
		createtb();
		tablesetting();

		init();
		createchkbox();
		// createcombox();

		TableColumn column = null;
		changeCellEditor();
		// changeCellEditor(table, column);

		this.setLocation(300, 300);
		this.setSize(size);
		this.add(tabpane);
		this.setVisible(true);
		this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);

	}

	public void changeCellEditor() {
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					modIntRow = table.getSelectedRow();
					int ch = Integer.valueOf((String) table.getValueAt(table.getSelectedRow(), 2));
					System.out.println(ch);
					JComboBox box = new JComboBox();
					int[] narray = null;
					for (int i = 0; i < initList.size(); i++) {
						num = Integer.parseInt(initList.get(i)[2]);
						narray = new int[num];
						for (int j = 1; j <= narray.length; j++) {
							if (num == ch) {
								box.addItem(j);
							}
						}
						table.getColumn("수량").setCellEditor(new DefaultCellEditor(box));
					}
				}
			}
		});
	}

	private void createchkbox() {
		JCheckBox box = new JCheckBox();
		box.setHorizontalAlignment(JLabel.CENTER);
		table.getColumn("체크").setCellEditor(new DefaultCellEditor(box));
		box.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					modIntRow = table.getSelectedRow();
					String in[] = new String[5];
					for (int i = 0; i < table.getColumnCount() - 1; i++) {
						in[i] = (String) table.getValueAt(table.getSelectedRow(), i);
						chk = 1;
					}
					System.out.println("chk: " + chk);
					in[4] = String.valueOf(table.getSelectedRow());

					if (chk == 1) {
						String[] sarray = new String[5];
						for (int i = 0; i < table.getColumnCount(); i++) {
							String g = String.valueOf(table.getValueAt(table.getSelectedRow(), i));
							sarray[i] = g;
						}
						goodsList.add(sarray);
					}

					for (int i = 0; i < in.length; i++) {
						in[i] = total.getText();
						total.setText("");
						int sum = Integer.parseInt(in[2]) * Integer.parseInt(in[3]);
						total.setText(String.valueOf(sum));
						total = (JTextField) table.getValueAt(table.getSelectedRow(), sum);

					}
					for (String[] a : goodsList) {
						for (int i = 0; i < a.length; i++) {
							System.out.println("sarray" + "[" + i + "]" + a[i]);
						}

					}
				}
			}
		});
	}

	private void makesDTO(String id, ArrayList<String[]> goodsList, int chk) {
		for (int j = 0; j < goodsList.size(); j++) {
			dto = new BasketlistDTO();
			dto.setId(id);
			int code = Integer.parseInt(goodsList.get(j)[0]);
			dto.setCode(code);
			dto.setCname(goodsList.get(j)[1]);
			int cnt = Integer.parseInt(goodsList.get(j)[2]);
			dto.setCnt(cnt);
			int price = Integer.parseInt(goodsList.get(j)[3]);
			dto.setPrice(price);
			dto.setCheck(chk);
			rlist.add(dto);
		}
		for (int j = 0; j < rlist.size(); j++) {
			System.out.println("----------------------------");
			System.out.println("rlist: " + rlist.get(j).getId());
		}

	}

	private void gotoInsert(BasketlistDTO dto) {
		for (int j = 0; j < rlist.size(); j++) {
			dao.Insert(rlist.get(j));
		}

	}

	private void init() {
		initList = gdao.getList();
		for (int i = 0; i < initList.size(); i++) {
			tablemodel.addRow(initList.get(i));
		}
		int rowNum = tablemodel.getRowCount();
		int colNum = tablemodel.getColumnCount();
	}

	public void tablesetting() {
		table.setRowMargin(0);
		table.getColumnModel().setColumnMargin(0);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);

		table.setShowVerticalLines(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		DefaultTableCellRenderer ts = new DefaultTableCellRenderer();// 테이블의 셀
																		// 값을 정렬
		ts.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tc = table.getColumnModel();
		for (int i = 0; i < tc.getColumnCount(); i++) {
			tc.getColumn(i).setCellRenderer(ts);
		}

	}

	private void createtb() {
		JLabel all = new JLabel("주문시 금액");
		south_north.add(all);
		south_north.add(total);

		JButton addB = new JButton("장바구니에 담기");
		south_north.add(addB);

		addB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				makesDTO(id, goodsList, chk);
				gotoInsert(dto);
				new BasketList(id);
			}

		});

		JButton modB = new JButton("바로 주문하기");
		south_north.add(modB);
		modB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				makesDTO2(id, goodsList, chk);
				gotoDbOrder(dto);
				new Orderlist(id);
				JOptionPane.showMessageDialog(null, "주문이 완료되었습니다.");
				dispose();

			}
		});

	}

	private void makesDTO2(String id, ArrayList<String[]> goodsList, int chk) {
		for (int j = 0; j < goodsList.size(); j++) {
			dto = new BasketlistDTO();
			dto.setId(id);
			int code = Integer.parseInt(goodsList.get(j)[0]);
			dto.setB_no(code);
			dto.setCname(goodsList.get(j)[1]);
			int cnt = Integer.parseInt(goodsList.get(j)[2]);
			dto.setCnt(cnt);
			int price = Integer.parseInt(goodsList.get(j)[3]);
			dto.setPrice(price);
			dto.setCheck(chk);
			rlist.add(dto);
		}
		for (int j = 0; j < rlist.size(); j++) {
			System.out.println("----------------------------");
			System.out.println("rlist: " + rlist.get(j).getId());
		}

	}

	private void gotoDbOrder(BasketlistDTO dto) {
		for (int i = 0; i < rlist.size(); i++) {
			boolean result = dao.insert2(rlist.get(i));
			System.out.println("그래서 주문이 되었니??????" + result);
		}

	}

	private void createpanel() {
		this.add(tab_center, "Center");
		this.add(tab_south, "South");

		tab_center.setLayout(new BorderLayout());
		tab_center.add(tableScroll, "Center");
		tab_center.add(tab_south, "South");
		tabpane.add("shopping", tab_center);

		tab_south.setLayout(new BorderLayout());
		tab_south.add(south_north, "North");

	}

}
