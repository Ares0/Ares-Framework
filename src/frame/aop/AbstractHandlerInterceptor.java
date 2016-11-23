package frame.aop;

import java.util.List;

import frame.utils.Utils;

public abstract class AbstractHandlerInterceptor implements HandlerInterceptor {

	private int level;
	
	private List<String> aspectExpression;
	
	public int getInterceptorLevel() {
		return level;
	}

	public void setInterceptorLevel(int level) {
		this.level = level;
	}

	public List<String> getAspectExpression() {
		return aspectExpression;
	}

	public void setAspectExpression(List<String> aspectExpressions) {
		this.aspectExpression = aspectExpressions;
	}

	@Override
	public boolean isMatchClass(String classExpr) {
		return isMatch(classExpr);
	}

	@Override
	public boolean isMatchBefore(String methodExpr) {
		return isMatch(methodExpr);
	}

	@Override
	public boolean isMatchAfter(String methodExpr) {
		return isMatch(methodExpr);
	}
	
	public boolean isMatch(String expr) {
		for (String exp : aspectExpression) {
			if (exp.contains(Utils.getLastNameByPeriod(expr))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(HandlerInterceptor hi) {
		return this.level - hi.getInterceptorLevel();
	}
	
}
